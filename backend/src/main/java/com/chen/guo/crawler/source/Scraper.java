package com.chen.guo.crawler.source;

import com.chen.guo.crawler.model.StockWebPage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

public interface Scraper {
  Logger logger = Logger.getLogger(Scraper.class);

  void doScraping(List<StockWebPage> pages, ScrapingTask scrapingTask) throws ConnectException;

  default void doAllScraping(ScrapingTask scrapingTask) {
    try {
      doScraping(getProfilePages(), scrapingTask);
    } catch (IOException e) {
      logger.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  List<StockWebPage> getProfilePages() throws IOException;
}
