package com.chen.guo.data.write.fs.orc;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.Writer;
import org.apache.orc.TypeDescription;

import java.io.IOException;

public abstract class AbstractOrcWriter<T> implements OrcWriter<T> {
  private TypeDescription _typeDescription;
  protected VectorizedRowBatch _vectorizedRowBatch;
  private Writer _writer;

  /**
   * Provide the schema type description
   */
  protected abstract TypeDescription createTypeDescription();

  /**
   * Provide the details of how to write each T obj
   */
  protected abstract void write(T obj);


  public AbstractOrcWriter(Path path, OrcFile.WriterOptions writerOptions) throws IOException {
    this(path, writerOptions, 1024);
  }

  public AbstractOrcWriter(Path path, OrcFile.WriterOptions writerOptions, int batchSize) throws IOException {
    TypeDescription schema = getTypeDescription();
    writerOptions.setSchema(schema);
    _writer = OrcFile.createWriter(path, writerOptions);
    _vectorizedRowBatch = schema.createRowBatch(batchSize);
  }

  @Override
  public void write(Iterable<T> objs) throws IOException {
    for (T obj : objs) {
      if (_vectorizedRowBatch.size == _vectorizedRowBatch.getMaxSize()) {
        _writer.addRowBatch(_vectorizedRowBatch);
        _vectorizedRowBatch.reset();
      }
      write(obj);
      _vectorizedRowBatch.size++;
    }
    close();
  }

  @Override
  public void close() throws IOException {
    if (_vectorizedRowBatch != null) {
      //Add the last un-full batch
      _writer.addRowBatch(_vectorizedRowBatch);
      _vectorizedRowBatch = null;
    }
    if (_writer != null) {
      _writer.close();
      _writer = null;
    }
  }

  @Override
  public TypeDescription getTypeDescription() {
    if (_typeDescription == null) {
      _typeDescription = createTypeDescription();
    }
    return _typeDescription;
  }
}
