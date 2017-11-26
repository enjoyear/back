package com.chen.guo.data.write.fs.orc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.orc.CompressionKind;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.orc.TypeDescription;

import java.io.Closeable;
import java.io.IOException;

public interface OrcWriter<T> extends Closeable {
  OrcWriter<T> write(Iterable<T> data) throws IOException;

  TypeDescription getTypeDescription();

  OrcWriter<T> create(Path path) throws IOException;

  OrcWriter<T> withConfiguration(Configuration configuration);

  OrcWriter<T> withOptions(OrcFile.WriterOptions writerOptions);

  OrcWriter<T> withCompression(CompressionKind compressionKind);

  OrcWriter<T> withBufferSize(int size);

  OrcWriter<T> withBatchSize(int batchSize);
}
