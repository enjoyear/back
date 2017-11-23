package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;

import java.io.IOException;

public interface CfiMenuNavigator {
  /**
   * Navigate to a menu page
   */
  String navigate(StockWebPage page) throws IOException;

  /**
   * Used for searching
   */
  String getNodeId();

  /**
   * Used for validation, could be used for searching as well. It's more complete than ROW_ID
   */
  String getName();
}
