package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.util.WebAccessor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.TreeMap;

public class CfiQuoteTask extends ScrapingTask<String, String> {
  public static final String LAST_QUOTE = "quote";
  public static final String CHG_VALUE = "chg_val";
  public static final String CHG_PERCENTAGE = "chg_pctg";
  private final StockWebPage _page;
  private final WebAccessor _accessor;

  public CfiQuoteTask(StockWebPage page, WebAccessor accessor){
    _page = page;
    _accessor = accessor;
  }


  @Override
  public TreeMap<String, String> scrape() throws IOException {
    //Try to get 财务分析指标 page
    String rootUrl = _page.getUrl();
    Document url行情首页 = _accessor.connect(rootUrl);
    Document url = url行情首页;
    Element quoteBlock = url.getElementById("last");
    TreeMap<String, String> quoteMap = new TreeMap<>();
    quoteMap.put(LAST_QUOTE, quoteBlock.text());
    Element changeBlock = url.getElementById("chg");
    quoteMap.put(CHG_VALUE, changeBlock.childNode(0).toString());
    quoteMap.put(CHG_PERCENTAGE, changeBlock.childNode(2).toString());
    return quoteMap;
  }

  @Override
  public StockWebPage getPage() {
    return null;
  }
}
