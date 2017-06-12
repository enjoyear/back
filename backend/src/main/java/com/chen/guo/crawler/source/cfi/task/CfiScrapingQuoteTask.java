package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.util.WebAccessUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.TreeMap;

public class CfiScrapingQuoteTask extends ScrapingTask<String, String> {
  public static final String LAST_QUOTE = "quote";
  public static final String CHG_VALUE = "chg_val";
  public static final String CHG_PERCENTAGE = "chg_pctg";

  @Override
  public void scrape(StockWebPage page) throws IOException {
    //Try to get 财务分析指标 page
    String rootUrl = page.getUrl();
    Document url行情首页 = WebAccessUtil.getInstance().getPageContent(rootUrl);
    Document url = url行情首页;
    Element quoteBlock = url.getElementById("last");
    TreeMap<String, String> quoteMap = new TreeMap<>();
    quoteMap.put(LAST_QUOTE, quoteBlock.text());
    Element changeBlock = url.getElementById("chg");
    quoteMap.put(CHG_VALUE, changeBlock.childNode(0).toString());
    quoteMap.put(CHG_PERCENTAGE, changeBlock.childNode(2).toString());
    results.put(page.getCode(), quoteMap);
  }
}
