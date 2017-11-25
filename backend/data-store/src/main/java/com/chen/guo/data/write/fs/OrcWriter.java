package com.chen.guo.data.write.fs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.Writer;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;

import java.io.IOException;
import java.util.Arrays;

/**
 * https://codecheese.wordpress.com/2016/04/20/writing-an-orc-file-using-java/
 * https://www.programcreek.com/java-api-examples/index.php?api=org.apache.hadoop.hive.ql.io.orc.OrcFile
 * https://github.com/eclecticlogic/eclectic-orc
 */
public class OrcWriter {
  public static void main(String[] args) throws IOException {
    String typeStr = "struct<a:string>";
    TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromTypeString(typeStr);
    ObjectInspector objInspector = TypeInfoUtils.getStandardJavaObjectInspectorFromTypeInfo(typeInfo);

    //ObjectInspector inspector = OrcStruct.createObjectInspector(typeInfo);

    Configuration conf = new Configuration();
    Path tempPath = new Path("/Users/chguo/Downloads/test.orc");

    Writer writer = OrcFile.createWriter(tempPath,
        OrcFile.writerOptions(conf).inspector(objInspector).stripeSize(100000).bufferSize(10000));
    writer.addRow(Arrays.asList("aaa"));
    writer.addRow(Arrays.asList("bbb"));
    writer.addRow(Arrays.asList("ccc"));
    writer.addRow(Arrays.asList("zzz"));
    writer.close();
  }
}
