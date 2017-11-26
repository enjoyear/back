package com.chen.guo.data.write.fs.orc;

import org.apache.orc.TypeDescription;

import java.io.Closeable;
import java.io.IOException;

public interface OrcWriter<T> extends Closeable {
  void write(Iterable<T> data) throws IOException;

  TypeDescription getTypeDescription();
}
