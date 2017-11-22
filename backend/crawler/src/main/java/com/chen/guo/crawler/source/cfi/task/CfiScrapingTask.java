package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;

import java.io.IOException;
import java.util.Map;

public interface CfiScrapingTask {
  String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

  StockWebPage getPage();

  String getMenuName();

  /**
   * Navigate to the menu
   *
   * @return the URL after navigation
   * @throws IOException
   */
  String navigate() throws IOException;

  Map<Integer, Map<String, Double>> scrape(String menuPage) throws IOException;

  default Map<Integer, Map<String, Double>> scrape() throws IOException {
    String url = navigate();
    return scrape(url);
  }
}
