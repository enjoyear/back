package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.util.WebAccessor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.TreeMap;

public abstract class CfiNetIncomeBaseTask extends ScrapingTask<Integer, Double> {
  //Used for searching.
  protected static final String ROW_ID = "归属母公司净利润";
  //Used for validation, could be used for searching as well. It's more complete than ROW_ID.
  protected static final String ROW_FULL_NAME = "归属母公司净利润（元）";
  //Specify the menu item for this task
  protected final CfiMenuClickTask _menuTask;

  protected final StockWebPage _page;
  protected final WebAccessor _accessor;

  protected CfiNetIncomeBaseTask(StockWebPage page, WebAccessor accessor) {
    _page = page;
    _accessor = accessor;
    _menuTask = new CfiMenuClickTask("nodea1", "财务分析指标", accessor);
  }

  @Override
  public StockWebPage getPage() {
    return _page;
  }

  @Override
  public TreeMap<Integer, Double> scrape() throws IOException {
    String pageToScrape = _menuTask.getPage(_page);
    return scrape(pageToScrape);
  }

  protected abstract TreeMap<Integer, Double> scrape(String url财务分析指标) throws IOException;

  /**
   * Get the page element for the table body of the main content
   *
   * @param page the page where the table body will be fetched
   */
  protected Element getMainTable(String page) throws IOException {
    Document doc = _accessor.connect(page);
    Element content = doc.getElementById("content");
    return content.getElementsByTag("table").first().getElementsByTag("tbody").first();
  }
}
