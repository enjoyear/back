package com.chen.guo.data.write.fs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.hadoop.hive.ql.io.orc.CompressionKind;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.Writer;
import org.apache.orc.TypeDescription;

import java.io.IOException;

public abstract class AbstractOrcWriter<T> implements OrcWriterCreator<T>, OrcWriter<T> {
  private Configuration _configuration = new Configuration();
  private OrcFile.WriterOptions _writerOptions;
  private CompressionKind _compressionKind;
  private int _bufferSize = 10 * 1024;  // This is the default buffer size
  private int _batchSize = 1024;        // This is the default batch size
  private TypeDescription _typeDescription;
  protected VectorizedRowBatch _vectorizedRowBatch;
  private Writer _writer;

  /**
   * Provide the schema type description
   */
  protected abstract TypeDescription createTypeDescription();

  /**
   * Provide the details of how to write each data T
   */
  protected abstract void write(T datum);

  @Override
  public OrcWriter<T> create(Path path) throws IOException {
    if (_writerOptions == null) {
      _writerOptions = OrcFile.writerOptions(_configuration);
    }
    if (_compressionKind != null) {
      _writerOptions.compress(_compressionKind);
    }
    if (_bufferSize != 0) {
      _writerOptions.bufferSize(_bufferSize);
    }
    TypeDescription schema = getTypeDescription();
    _writerOptions.setSchema(schema);

    _writer = OrcFile.createWriter(path, _writerOptions);

    _vectorizedRowBatch = schema.createRowBatch(_batchSize);
    return this;
  }

  @Override
  public AbstractOrcWriter<T> write(Iterable<T> data) throws IOException {
    for (T d : data) {
      if (_vectorizedRowBatch.size == _vectorizedRowBatch.getMaxSize()) {
        _writer.addRowBatch(_vectorizedRowBatch);
        _vectorizedRowBatch.reset();
      }
      // Write the d to the column vectors.
      write(d);
      _vectorizedRowBatch.size++;
    }
    return this;
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
  public OrcWriterCreator<T> withConfiguration(Configuration configuration) {
    _configuration = configuration;
    return this;
  }

  @Override
  public OrcWriterCreator<T> withOptions(OrcFile.WriterOptions writerOptions) {
    _writerOptions = writerOptions;
    return this;
  }

  @Override
  public OrcWriterCreator<T> withCompression(CompressionKind compressionKind) {
    _compressionKind = compressionKind;
    return this;
  }

  @Override
  public OrcWriterCreator<T> withBufferSize(int size) {
    _bufferSize = size;
    return this;
  }

  @Override
  public OrcWriterCreator<T> withBatchSize(int batchSize) {
    _batchSize = batchSize;
    return this;
  }

  @Override
  public TypeDescription getTypeDescription() {
    if (_typeDescription == null) {
      _typeDescription = createTypeDescription();
    }
    return _typeDescription;
  }
}
