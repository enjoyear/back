package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessUtil;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.rmi.UnexpectedException;

class CfiScrapingMenuTask {

  private final String _nodeId;
  private final String _menuName;

  public CfiScrapingMenuTask(String nodeId, String menuName) {
    _nodeId = nodeId;
    _menuName = menuName;
  }

  public String getMenuPage(StockWebPage page) throws IOException {
    String rootUrl = page.getUrl();
    Element pageFoundamentalIndicators = WebAccessUtil.getInstance().getPageContent(rootUrl)
        .getElementById(_nodeId);
    Element nonbreakableFI = pageFoundamentalIndicators.getElementsByTag("nobr").first();
    if (!_menuName.equals(nonbreakableFI.text()))
      throw new UnexpectedException(String.format("Didn't get the correct page for %s: %s", _menuName, rootUrl));
    return WebAccessUtil.getHyperlink(nonbreakableFI);
  }
}
