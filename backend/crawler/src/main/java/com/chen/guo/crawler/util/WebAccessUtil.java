package com.chen.guo.crawler.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class WebAccessUtil {
  public static final int DEFAULT_CONNECTION_TIMEOUT = 5;
  private static WebAccessUtil INSTANCE_DEFAULT;

  private final int _connectionTimeout;

  static {
    INSTANCE_DEFAULT = new WebAccessUtil();
  }

  /**
   * @return the WebAccessUtil instance with default connection timeout of 5 seconds.
   */
  public static WebAccessUtil getInstance() {
    return INSTANCE_DEFAULT;
  }

  private WebAccessUtil() {
    this(DEFAULT_CONNECTION_TIMEOUT);
  }

  public WebAccessUtil(Integer internetTimeoutSecond) {
    _connectionTimeout = internetTimeoutSecond * 1000;
  }

  public Document getPageContent(String url) throws IOException {
    Connection connect = Jsoup.connect(url);
    connect.timeout(_connectionTimeout);
    return connect.get();
  }

  public static String getHyperlink(Element element) {
    return element.getElementsByTag("a").get(0).absUrl("href");
  }
}
