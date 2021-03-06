package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessor;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.rmi.UnexpectedException;

public class CfiMenuClickTask implements CfiMenuNavigator {

  private final String _nodeId;
  private final String _name;
  private WebAccessor _webAccessor;

  /**
   * @param nodeId the <DIV> tag id for current task on the left-hand side menu
   * @param name   the name on the menu for validation
   */
  public CfiMenuClickTask(String nodeId, String name, WebAccessor webAccessor) {
    _nodeId = nodeId;
    _name = name;
    _webAccessor = webAccessor;
  }

  /**
   * Serves as an action to click into an analysis page from the left-hand side menu
   *
   * @return the URL after clicking
   */
  public String navigate(StockWebPage page) throws IOException {
    String rootUrl = page.getUrl();
    Element nodeElement = _webAccessor.connect(rootUrl).getElementById(_nodeId);
    Element clickLine = nodeElement.getElementsByTag("nobr").first();
    if (!_name.equals(clickLine.text())) {
      throw new UnexpectedException(String.format(
          "Expected the item to be %s for <DIV> with ID %s at page %s, but the actual name is %s.",
          _name, _nodeId, rootUrl, clickLine.text()));
    }
    return WebAccessor.getHyperlink(clickLine);
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
