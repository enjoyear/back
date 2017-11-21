package com.chen.guo.crawler.source;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.cfi.task.collector.ResultCollector;
import com.chen.guo.crawler.source.cfi.task.creator.TaskCreator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

public interface Scraper {
  Logger logger = Logger.getLogger(Scraper.class);

  /**
   * @return a list of urls for each stock
   */
  List<StockWebPage> getProfilePages() throws IOException;

  void doScraping(List<StockWebPage> pages, TaskCreator<Integer, Double> taskCreator, ResultCollector collector) throws Exception;

  default void doAllScraping(TaskCreator<Integer, Double> taskCreator, ResultCollector collector) {
    try {
      doScraping(getProfilePages(), taskCreator, collector);
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
