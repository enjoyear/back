import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.OrcFile.WriterOptions;
import org.apache.hadoop.hive.ql.io.orc.Writer;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;


public class OrcWriterTest {
  private final Configuration conf = new Configuration();

  @Test
  public void typical() throws IOException {
    TypeInfo typeInfo = TypeInfoUtils.getTypeInfoFromTypeString("struct<a:string>");
    ObjectInspector inspector = TypeInfoUtils.getStandardJavaObjectInspectorFromTypeInfo(typeInfo);
    WriterOptions options = OrcFile.writerOptions(conf).inspector(inspector);

    Path path = new Path("/tmp/test.orc");

    Writer writer = OrcFile.createWriter(path, options);
    writer.addRow(Arrays.asList("hello"));
    writer.close();

  }
}