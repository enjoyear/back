package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.common.date.DateTimeUtil;
import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessor;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.time.LocalDate;
import java.util.TreeMap;

public class CfiNetIncomeTaskHist extends CfiNetIncomeBaseTask {
  private static final Logger logger = Logger.getLogger(CfiNetIncomeTaskHist.class);
  private final int _startYear;

  /**
   * Get all historical data
   */
  public CfiNetIncomeTaskHist(StockWebPage page, WebAccessor accessor) {
    this(-1, page, accessor);
  }

  /**
   * @param startYear denotes the oldest year we care about. This startYear is inclusive
   */
  public CfiNetIncomeTaskHist(int startYear, StockWebPage page, WebAccessor accessor) {
    super(page, accessor);
    _startYear = startYear;
  }


  @Override
  public TreeMap<Integer, Double> scrapeMenuPage(String url) throws IOException {
    Element mainTableBody = getMainTable(url);
    Element netProfitTr = mainTableBody.getElementsContainingOwnText(ROW_ID).first();
    String npPage = netProfitTr.absUrl("href");
    Document netProfitPage = _accessor.connect(npPage);
    //Get all historical
    Elements rows = netProfitPage.getElementById("content").getElementsByTag("tbody").first().children();

    Element header = rows.get(1);
    //Do validation with the header
    if (!header.child(0).text().equals("报告期") || !header.child(1).text().equals(ROW_FULL_NAME)) {
      throw new UnexpectedException("Layout of the table seem to be changed for: " + npPage);
    }

    TreeMap<Integer, Double> data = new TreeMap<>();
    //Skip first two lines of headers, and last empty line.
    for (int r = 2; r < rows.size() - 1; ++r) {
      Element row = rows.get(r);
      Elements children = row.children();
      LocalDate date = DateTimeUtil.getDate(children.get(0).text(), DEFAULT_DATE_PATTERN);
      if (date.getYear() >= _startYear) {
        data.put(date.getYear() * 100 + date.getMonthValue(), Double.valueOf(children.get(1).text()));
      } else {
        break;
      }
    }
    return data;
  }
}
