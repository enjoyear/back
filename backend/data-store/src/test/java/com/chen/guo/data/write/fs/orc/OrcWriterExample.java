package com.chen.guo.data.write.fs.orc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.BytesColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.LongColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.MapColumnVector;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.orc.TypeDescription;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class OrcWriterExample extends AbstractOrcWriter<OrcWriterExample.ExamplePOJO> {
  private final int BATCH_SIZE;

  private final BytesColumnVector _firstVector;
  private final LongColumnVector _secondVector;
  private final MapColumnVector _mapVector;
  private final BytesColumnVector _mapKeyVector;
  private final LongColumnVector _mapValueVector;
  private int _maxMapSize;

  public OrcWriterExample(Path path, OrcFile.WriterOptions writerOptions) throws IOException {
    this(path, writerOptions, 1);
  }

  public OrcWriterExample(Path path, OrcFile.WriterOptions writerOptions, int maxMapSize) throws IOException {
    super(path, writerOptions, 3);
    _maxMapSize = maxMapSize;
    BATCH_SIZE = _vectorizedRowBatch.getMaxSize();

    _firstVector = (BytesColumnVector) _vectorizedRowBatch.cols[0];
    _secondVector = (LongColumnVector) _vectorizedRowBatch.cols[1];

    _mapVector = (MapColumnVector) _vectorizedRowBatch.cols[2];
    _mapKeyVector = (BytesColumnVector) _mapVector.keys;
    _mapValueVector = (LongColumnVector) _mapVector.values;
    _mapKeyVector.ensureSize(maxMapSize * BATCH_SIZE, false);
    _mapValueVector.ensureSize(maxMapSize * BATCH_SIZE, false);
  }

  @Override
  protected TypeDescription createTypeDescription() {
    return TypeDescription.createStruct()
        .addField("first", TypeDescription.createString())
        .addField("second", TypeDescription.createInt())
        .addField("third", TypeDescription.createMap(TypeDescription.createString(), TypeDescription.createInt()));
  }

  @Override
  protected void write(ExamplePOJO obj) {
    int row = _vectorizedRowBatch.size;
    _firstVector.setRef(row, obj._first.getBytes(), 0, obj._first.length());
    _secondVector.vector[row] = obj._second;

    int mapStart = _mapVector.childCount;
    int mapSize = obj._third.size();
    _mapVector.offsets[row] = mapStart;
    _mapVector.lengths[row] = mapSize;
    _mapVector.childCount += mapSize;

    if (obj._third.size() > _maxMapSize) {
      System.out.println(String.format("Increase _maxMapSize from %d to %d. This will have performance impact.", _maxMapSize, obj._third.size()));
      _maxMapSize = obj._third.size();
      _mapKeyVector.ensureSize(_maxMapSize * _vectorizedRowBatch.getMaxSize(), true);
      _mapValueVector.ensureSize(_maxMapSize * _vectorizedRowBatch.getMaxSize(), true);
    }

    for (Map.Entry<String, Integer> mapItem : obj._third.entrySet()) {
      String key = mapItem.getKey();
      _mapKeyVector.setRef(mapStart, key.getBytes(), 0, key.length());
      _mapValueVector.vector[mapStart] = mapItem.getValue();
      ++mapStart;
    }
  }

  public static void main(String[] args) throws IOException {
    OrcFile.WriterOptions options = OrcFile.writerOptions(new Configuration());
    String pathname = "/tmp/test.orc";
    File file = new File(pathname);
    file.delete();
    Path path = new Path(pathname);
    OrcWriterExample writer = new OrcWriterExample(path, options);

    Map<String, Integer> map1 = new HashMap<>();
    map1.put("k1", 1);
    Map<String, Integer> map2 = new HashMap<>();
    Map<String, Integer> map3 = new HashMap<>();
    map3.put("k2", 2);
    Map<String, Integer> map4 = new HashMap<>();
    map4.put("k1", 1);
    map4.put("k2", 2);
    Map<String, Integer> map5 = new HashMap<>();
    map5.put("k3", 3);
    Map<String, Integer> map6 = new HashMap<>();
    map6.put("k1", 1);
    map6.put("k2", 2);
    map6.put("k3", 3);

    writer.write(Arrays.asList(
        new ExamplePOJO("obj1", 1, map1),
        new ExamplePOJO("obj2", 2, map2),
        new ExamplePOJO("obj3", 3, map3),
        new ExamplePOJO("obj4", 4, map4),
        new ExamplePOJO("obj5", 5, map5),
        new ExamplePOJO("obj6", 6, map6)
    ));
  }

  public static class ExamplePOJO {
    private final String _first;
    private final int _second;
    private final Map<String, Integer> _third;

    public ExamplePOJO(String first, int second, Map<String, Integer> third) {
      _first = first;
      _second = second;
      _third = third;
    }
  }
}