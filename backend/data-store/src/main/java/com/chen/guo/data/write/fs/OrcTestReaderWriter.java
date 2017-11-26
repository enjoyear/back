package com.chen.guo.data.write.fs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.BytesColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.LongColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.MapColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.Reader;
import org.apache.hadoop.hive.ql.io.orc.RecordReader;
import org.apache.hadoop.hive.ql.io.orc.Writer;
import org.apache.orc.TypeDescription;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * https://orc.apache.org/docs/core-java.html
 * <p>
 * https://codecheese.wordpress.com/2017/06/13/reading-and-writing-orc-files-using-vectorized-row-batch-in-java/
 * https://www.programcreek.com/java-api-examples/index.php?api=org.apache.hadoop.hive.ql.io.orc.OrcFile
 * https://github.com/eclecticlogic/eclectic-orc
 */
public class OrcTestReaderWriter {
  final static String outputPath = "/Users/chguo/Downloads/test.orc";

  public static void main(String[] args) throws IOException {
    write();
    read();
  }

  public static void read() throws IOException {
    Configuration conf = new Configuration();
    Reader reader = OrcFile.createReader(new Path(outputPath),
        OrcFile.readerOptions(conf));
    System.out.println("Number of rows: " + reader.getNumberOfRows());
    System.out.println("Compression: " + reader.getCompression());

    RecordReader rows = reader.rows();
    VectorizedRowBatch batch = reader.getSchema().createRowBatch();
    while (rows.nextBatch(batch)) {
      BytesColumnVector firstCol = (BytesColumnVector) batch.cols[0];
      LongColumnVector secondCol = (LongColumnVector) batch.cols[1];
      MapColumnVector thirdCol = (MapColumnVector) batch.cols[2];
      BytesColumnVector mapKey = (BytesColumnVector) thirdCol.keys;
      LongColumnVector mapValue = (LongColumnVector) thirdCol.values;

      for (int r = 0; r < batch.size; ++r) {
        StringBuilder sb = new StringBuilder();
        /**
         * Alternatively, you can use stringifyValue
         */
        //firstCol.stringifyValue(new StringBuilder(), r);
        String col1 = new String(firstCol.vector[r], firstCol.start[r], firstCol.length[r]);
        long col2 = secondCol.vector[r];
        sb.append(String.format("%s , %d , map(", col1, col2));
        for (long start = thirdCol.offsets[r]; start < thirdCol.offsets[r] + thirdCol.lengths[r]; ++start) {
          int s = (int) start;
          String keyString = new String(mapKey.vector[s], mapKey.start[s], mapKey.length[s]);
          long valString = mapValue.vector[s];
          sb.append(String.format(" %s -> %d ,", keyString, valString));
        }
        sb.append(")\n");
        System.out.println(sb.toString());
      }
    }
    rows.close();
  }

  public static void write() throws IOException {
    new File(outputPath).delete();

    Path testFilePath = new Path(outputPath);
    Configuration conf = new Configuration();

//    TypeDescription schema =
//        TypeDescription.fromString("struct<first:string," +
//            "second:int,third:map<string,int>>");

    TypeDescription schema = TypeDescription.createStruct()
        .addField("first", TypeDescription.createString())
        .addField("second", TypeDescription.createInt())
        .addField("third", TypeDescription.createMap(TypeDescription.createString(), TypeDescription.createInt()));

    Writer writer =
        OrcFile.createWriter(testFilePath,
            OrcFile.writerOptions(conf).setSchema(schema));

    VectorizedRowBatch batch = schema.createRowBatch();
    /**
     * See the doc for BytesColumnVector
     * https://orc.apache.org/api/hive-storage-api/index.html?help-doc.html
     */
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

      /**
       * Performance is bad to setVal, which copies data into a local buffer
       */
      //first.setVal(row, String.format("Row: %d", r).getBytes());
      byte[] bytes = String.format("row: %d", r).getBytes();
      first.setRef(row, bytes, 0, bytes.length);
      second.vector[row] = r * 3;

      map.offsets[row] = map.childCount;
      map.lengths[row] = MAP_SIZE;
      map.childCount += MAP_SIZE;

      for (int mapElem = (int) map.offsets[row];
           mapElem < map.offsets[row] + MAP_SIZE; ++mapElem) {
        String key = "row " + r + "." + (mapElem - map.offsets[row]);
        /**
         * Use setRef to improve performance
         */
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
