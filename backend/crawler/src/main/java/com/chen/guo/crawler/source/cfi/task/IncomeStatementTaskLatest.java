package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.common.date.DateTimeUtil;
import com.chen.guo.common.number.DoubleUtil;
import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class IncomeStatementTaskLatest extends IncomeStatementTask {
  public IncomeStatementTaskLatest(StockWebPage page, WebAccessor accessor, Set<String> rowNames) {
    super(page, accessor, rowNames);
  }

  @Override
  public TreeMap<Integer, Map<String, Double>> scrape(String menuPage) throws IOException {
    Element table = getMainTable(menuPage);
    Elements headerRow = validateHeader(menuPage, table);

    Set<String> rowsWanted = new HashSet<>(_rowNames);
    HashMap<String, Elements> selectedRows = new HashMap<>();
    for (Element row : table.children()) {
      Elements children = row.children();
      String rowName = children.get(0).text().trim();
      if (rowsWanted.contains(rowName)) {
        selectedRows.put(rowName, children);
        rowsWanted.remove(rowName);
      }
      if (rowsWanted.isEmpty())
        break;
    }

    TreeMap<Integer, Map<String, Double>> results = new TreeMap<>();
    for (int i = 1; i < headerRow.size(); ++i) {
      int yearMonthInt = DateTimeUtil.getYearMonthInt(headerRow.get(i).text(), DEFAULT_DATE_PATTERN);
      Map<String, Double> quarterlyNumbers = new HashMap<>();
      for (Map.Entry<String, Elements> row : selectedRows.entrySet()) {
        String rowName = row.getKey();
        Elements columns = row.getValue();
        quarterlyNumbers.put(rowName, DoubleUtil.parse(columns.get(i).text()));
      }
      results.put(yearMonthInt, quarterlyNumbers);
    }
    return results;
  }


  private Elements validateHeader(String menuPage, Element table) {
    Element yearMonthTr = table.getElementsByTag("tr").get(1);
    Elements headerRow = yearMonthTr.children();
    if (!headerRow.first().ownText().equals("截止日期")) {
      throw new RuntimeException(String.format("Header validation failed: Expected the header line 截止日期, but getting %s for the url %s",
          headerRow.first().ownText(), menuPage));
    }
    return headerRow;
  }
}
