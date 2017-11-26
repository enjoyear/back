package com.chen.guo.data.write.fs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.orc.CompressionKind;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;

import java.io.IOException;

public interface OrcWriterCreator<T> {
  OrcWriter<T> create(Path path) throws IOException;

  OrcWriterCreator<T> withConfiguration(Configuration configuration);

  OrcWriterCreator<T> withOptions(OrcFile.WriterOptions writerOptions);

  OrcWriterCreator<T> withCompression(CompressionKind compressionKind);

  OrcWriterCreator<T> withBufferSize(int size);

  OrcWriterCreator<T> withBatchSize(int batchSize);
}
