package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.util.WebAccessUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public abstract class CfiScrapingNetIncomeTask extends ScrapingTask<Integer, Double> {

  protected final CfiScrapingMenuTask _menuTask;

  protected CfiScrapingNetIncomeTask() {
    _menuTask = new CfiScrapingMenuTask("nodea1", "财务分析指标");
  }

  @Override
  public void scrape(StockWebPage page) throws IOException {
    scrape(page.getCode(), _menuTask.getMenuPage(page));
  }

  protected abstract void scrape(String ticker, String url财务分析指标) throws IOException;

  protected Element getMainTable(String baseUrl) throws IOException {
    Document doc = WebAccessUtil.getInstance().getPageContent(baseUrl);
    Element content = doc.getElementById("content");
    return content.getElementsByTag("table").first().getElementsByTag("tbody").first();
  }
}
