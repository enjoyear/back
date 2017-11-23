package com.chen.guo.crawler.source.cfi.task.unifier;

import java.util.function.Function;

public class MetricUnifier {
  private final String _rowName;
  private final String _uniformName;
  private final Function<Double, Double> _valueUnifier;

  MetricUnifier(String rowName, String uniformName, Function<Double, Double> valueUnifier) {
    _rowName = rowName;
    _uniformName = uniformName;
    _valueUnifier = valueUnifier;
  }

  public String getRowName() {
    return _rowName;
  }

  public String getUniformName() {
    return _uniformName;
  }

  public Function<Double, Double> getValueUnifier() {
    return _valueUnifier;
  }
}
