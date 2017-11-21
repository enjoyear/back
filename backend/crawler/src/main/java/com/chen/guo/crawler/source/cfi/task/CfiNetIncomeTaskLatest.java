package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessor;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.TreeMap;

public class CfiNetIncomeTaskLatest extends CfiNetIncomeBaseTask {
  private static final Logger logger = Logger.getLogger(CfiNetIncomeTaskLatest.class);

  public CfiNetIncomeTaskLatest(StockWebPage page, WebAccessor accessor) {
    super(page, accessor);
  }

  @Override
  public TreeMap<Integer, Double> scrape(String url财务分析指标) throws IOException {
    String baseUrl = url财务分析指标;
    logger.info("Scraping page: " + baseUrl);
    Element table = getMainTable(baseUrl);
    Element yearMonthTr = table.getElementsByTag("tr").get(1);
    if (yearMonthTr.getElementsContainingOwnText("截止日期").size() != 1)
      throw new RuntimeException("Didn't get correct line for 截止日期");
    yearMonthTr.children().forEach(c -> System.out.println(c.text()));
    Element netProfitTr = table.getElementsContainingOwnText(ROW_ID).first();
    //Get from main page.
    //TODO: add scraped results to "results" variable
    netProfitTr.parent().parent().children().forEach(c -> System.out.println(c.text()));
    return null;
  }
}
