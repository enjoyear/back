package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessor;

import java.io.IOException;

public class CfiMenuNoopTask implements CfiMenuNavigator {

  private final String _nodeId;
  private final String _name;
  private String _navigatedUrl;
  private WebAccessor _webAccessor;

  public CfiMenuNoopTask(String nodeId, String name, String navigatedUrl) {
    _nodeId = nodeId;
    _name = name;
    _navigatedUrl = navigatedUrl;
  }

  /**
   * Serves as an action to click into an analysis page from the left-hand side menu
   *
   * @return the URL after clicking
   */
  public String navigate(StockWebPage page) throws IOException {
    return _navigatedUrl;
  }

  @Override
  public String getNodeId() {
    return _nodeId;
  }

  @Override
  public String getName() {
    return _name;
  }
}
