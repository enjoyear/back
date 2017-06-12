package com.chen.guo.crawler;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class CrawlerConfig {

  /**
   * It's actually static
   */
  private final Config config;

  private CrawlerConfig() {
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("crawler.conf").getFile());
    config = ConfigFactory.parseFile(file).resolve();
  }

  /**
   * Following "Initialization-on-demand holder idiom"
   * https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
   */
  private static class CrawlerConfigHolder {
    private static final CrawlerConfig INSTANCE = new CrawlerConfig();
  }

  public static Config getConfig() {
    return CrawlerConfigHolder.INSTANCE.config;
  }
}
