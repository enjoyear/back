package com.chen.guo.data.write.fs;

import org.apache.orc.TypeDescription;

import java.io.Closeable;
import java.io.IOException;

public interface OrcWriter<T> extends Closeable {
  OrcWriter<T> write(Iterable<T> data) throws IOException;

  TypeDescription getTypeDescription();
}
