package com.chen.guo.data.write.fs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.BytesColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.LongColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.MapColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.Writer;
import org.apache.orc.TypeDescription;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * https://orc.apache.org/docs/core-java.html
 * <p>
 * https://codecheese.wordpress.com/2016/04/20/writing-an-orc-file-using-java/
 * https://www.programcreek.com/java-api-examples/index.php?api=org.apache.hadoop.hive.ql.io.orc.OrcFile
 * https://github.com/eclecticlogic/eclectic-orc
 */
public class OrcWriter {
  final static String outputPath = "/Users/chguo/Downloads/test.orc";

  public static void main(String[] args) throws IOException {
    new File(outputPath).delete();

    write();

//    Configuration conf = new Configuration();
//    Reader reader = OrcFile.createReader(new Path(),
//        OrcFile.readerOptions(conf));
//
//    RecordReader rows = reader.rows();
//    VectorizedRowBatch batch = reader.getSchema().createRowBatch();
//    while (rows.nextBatch(batch)) {
//      for(int r=0; r < batch.size; ++r) {
//        rows.next(null).
//      }
//    }
//    rows.close();
  }

  public static void write() throws IOException {
    Path testFilePath = new Path(outputPath);
    Configuration conf = new Configuration();

    TypeDescription schema =
        TypeDescription.fromString("struct<first:string," +
            "second:int,third:map<string,int>>");

    Writer writer =
        OrcFile.createWriter(testFilePath,
            OrcFile.writerOptions(conf).setSchema(schema));

    VectorizedRowBatch batch = schema.createRowBatch();
    BytesColumnVector first = (BytesColumnVector) batch.cols[0];
    LongColumnVector second = (LongColumnVector) batch.cols[1];

//Define map. You need also to cast the key and value vectors
    MapColumnVector map = (MapColumnVector) batch.cols[2];
    BytesColumnVector mapKey = (BytesColumnVector) map.keys;
    LongColumnVector mapValue = (LongColumnVector) map.values;

// Each map has 5 elements
    final int MAP_SIZE = 5;
    final int BATCH_SIZE = batch.getMaxSize();

// Ensure the map is big enough
    mapKey.ensureSize(BATCH_SIZE * MAP_SIZE, false);
    mapValue.ensureSize(BATCH_SIZE * MAP_SIZE, false);

// add 1500 rows to file
    for (int r = 0; r < 150; ++r) {
      int row = batch.size++;

      first.setVal(row, String.format("Row: %d", r).getBytes());
      second.vector[row] = r * 3;

      map.offsets[row] = map.childCount;
      map.lengths[row] = MAP_SIZE;
      map.childCount += MAP_SIZE;

      for (int mapElem = (int) map.offsets[row];
           mapElem < map.offsets[row] + MAP_SIZE; ++mapElem) {
        String key = "row " + r + "." + (mapElem - map.offsets[row]);
        mapKey.setVal(mapElem, key.getBytes(StandardCharsets.UTF_8));
        mapValue.vector[mapElem] = mapElem;
      }
      if (row == BATCH_SIZE - 1) {
        writer.addRowBatch(batch);
        batch.reset();
      }
    }
    if (batch.size != 0) {
      writer.addRowBatch(batch);
      batch.reset();
    }
    writer.close();
  }


}
