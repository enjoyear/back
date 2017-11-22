package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.common.DateTimeUtil;
import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessor;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

public class CfiNetIncomeTaskLatest extends CfiNetIncomeBaseTask {
  private static final Logger logger = Logger.getLogger(CfiNetIncomeTaskLatest.class);

  public CfiNetIncomeTaskLatest(StockWebPage page, WebAccessor accessor) {
    super(page, accessor);
  }

  @Override
  public TreeMap<Integer, Double> scrapeMenuPage(String menuPage) throws IOException {
    Element table = getMainTable(menuPage);
    Element yearMonthTr = table.getElementsByTag("tr").get(1);
    Elements headerRow = yearMonthTr.children();
    if (!headerRow.first().ownText().equals("截止日期")) {
      throw new RuntimeException(String.format("Expected the header line 截止日期, but getting %s for the url %s",
          headerRow.first().ownText(), menuPage));
    }

    Element netProfitTr = table.getElementsContainingOwnText(ROW_ID).first();
    Elements netProfitRow = netProfitTr.parent().parent().children();
    for (int i = 1; i < headerRow.size(); ++i) {
      System.out.println(DateTimeUtil.getYearMonthInt(headerRow.get(i).text(), DEFAULT_DATE_PATTERN) + " " + netProfitRow.get(i).text());
    }
    return null;
  }
}
