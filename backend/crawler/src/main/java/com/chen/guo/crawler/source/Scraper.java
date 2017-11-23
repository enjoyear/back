package com.chen.guo.crawler.source;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.cfi.task.collector.ResultCollector;
import com.chen.guo.crawler.source.cfi.task.creator.TaskCreator;
import com.chen.guo.crawler.util.WebAccessor;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public interface Scraper {
  Logger logger = Logger.getLogger(Scraper.class);
  Pattern ALL_DIGITS = Pattern.compile("\\d{6}");
  WebAccessor WEB_ACCESSOR = WebAccessor.getDefault();

  /**
   * @return a list of urls for each stock
   */
  List<StockWebPage> getProfilePages() throws IOException;

  void doScraping(List<StockWebPage> pages, TaskCreator taskCreator, ResultCollector collector) throws Exception;

  default void doAllScraping(List<StockWebPage> pages, TaskCreator taskCreator, ResultCollector collector) {
    try {
      doScraping(getProfilePages(), taskCreator, collector);
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  /**
   * Exclude all ST, *ST, s*st
   */
  static boolean checkForSWPCriteria(StockWebPage sp) {
    String code = sp.getCode();
    if (code.startsWith("0") ||
        code.startsWith("6") ||
        (code.startsWith("3") && !code.startsWith("39"))) {
      if (checkForCodeCriteria(code)) {
        //Exclude when the name is too long
        if (sp.getName().length() > 4 &&
            !sp.getName().equals("TCL集团")) {
          if (!sp.getName().toLowerCase().startsWith("*st"))
            logger.info("Exclude: " + sp);
          return false;
        }

        //Exclude ST
        if (sp.getName().toLowerCase().startsWith("st")) {
          return false;
        }

        return true;
      }
    }
    return false;
  }

  /**
   * Make sure a 6-digit code
   */
  static boolean checkForCodeCriteria(String str) {
    return ALL_DIGITS.matcher(str).matches();
  }
}
