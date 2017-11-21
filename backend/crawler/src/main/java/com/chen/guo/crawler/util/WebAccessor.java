package com.chen.guo.crawler.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class WebAccessor {
  public static final int DEFAULT_CONNECTION_TIMEOUT = 5;
  private static WebAccessor INSTANCE_DEFAULT;

  private final int _connectionTimeout;

  static {
    INSTANCE_DEFAULT = new WebAccessor();
  }

  /**
   * @return the WebAccessor instance with default connection timeout of 5 seconds.
   */
  public static WebAccessor getDefault() {
    return INSTANCE_DEFAULT;
  }

  private WebAccessor() {
    this(DEFAULT_CONNECTION_TIMEOUT);
  }

  public WebAccessor(Integer internetTimeoutSecond) {
    _connectionTimeout = internetTimeoutSecond * 1000;
  }

  public Document connect(String url) throws IOException {
    Connection connect = Jsoup.connect(url);
    connect.timeout(_connectionTimeout);
    return connect.get();
  }

  public static String getHyperlink(Element element) {
    return element.getElementsByTag("a").get(0).absUrl("href");
  }
}
