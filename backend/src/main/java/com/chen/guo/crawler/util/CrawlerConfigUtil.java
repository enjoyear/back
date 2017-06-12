package com.chen.guo.crawler.util;

import com.chen.guo.crawler.CrawlerConfig;

import java.time.LocalDateTime;

public class CrawlerConfigUtil {

  public static int getStartingYear() {
    LocalDateTime now = LocalDateTime.now();
    return now.getYear() - CrawlerConfig.getConfig().getInt("number_of_years_back");
  }

}
