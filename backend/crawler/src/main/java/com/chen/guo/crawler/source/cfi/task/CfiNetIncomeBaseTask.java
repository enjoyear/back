package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.util.WebAccessUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public abstract class CfiNetIncomeBaseTask extends ScrapingTask<Integer, Double> {

  protected final CfiMenuClickTask _menuTask;

  protected CfiNetIncomeBaseTask() {
    _menuTask = new CfiMenuClickTask("nodea1", "财务分析指标");
  }

  @Override
  public void scrape(StockWebPage page) throws IOException {
    String pageToScrape = _menuTask.getPage(page);
    scrape(page.getCode(), pageToScrape);
  }

  protected abstract void scrape(String ticker, String url财务分析指标) throws IOException;

  /**
   * Get the page element for the table body of the main content
   *
   * @param page the page where the table body will be fetched
   */
  protected Element getMainTable(String page) throws IOException {
    Document doc = WebAccessUtil.getInstance().getPageContent(page);
    Element content = doc.getElementById("content");
    return content.getElementsByTag("table").first().getElementsByTag("tbody").first();
  }
}
