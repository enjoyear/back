package com.chen.guo.crawler.model;

import java.io.Serializable;

public class StockWebPage implements Serializable {
  private final String _name;
  private final String _code;
  private final String _url;

  public StockWebPage(String name, String code, String url) {
    _name = name;
    _code = code;
    _url = url;
  }

  public String getName() {
    return _name;
  }

  public String getCode() {
    return _code;
  }

  public String getUrl() {
    return _url;
  }

  @Override
  public String toString() {
    return String.format("SWP{'%s','%s','%s'}", _code, _name, _url);
  }
}
