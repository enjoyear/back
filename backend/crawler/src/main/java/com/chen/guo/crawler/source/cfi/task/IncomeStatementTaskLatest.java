package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.common.date.DateTimeUtil;
import com.chen.guo.common.number.DoubleUtil;
import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class IncomeStatementTaskLatest extends IncomeStatementTask {

  /**
   * @param page     the StockWebPage
   * @param accessor
   * @param rowNames provide a set of rows you want to fetch
   */
  public IncomeStatementTaskLatest(StockWebPage page, WebAccessor accessor, Set<String> rowNames) {
    super(page, accessor, rowNames);
  }

  @Override
  public TreeMap<Integer, Map<String, Double>> scrape(String menuPage) throws IOException {
    Element table = connectAndGetContentTable(menuPage);
    Elements headerRow = validateHeader(menuPage, table);
    HashMap<String, Element> selectedRows = getSelectedRows(table);

    //Add all wanted rows' values to a map
    TreeMap<Integer, Map<String, Double>> results = new TreeMap<>();
    for (int col = 1; col < headerRow.size(); ++col) {
      int yearMonthInt = DateTimeUtil.getYearMonthInt(headerRow.get(col).text(), DEFAULT_DATE_PATTERN);
      Map<String, Double> quarterlyNumbers = new HashMap<>();
      for (Map.Entry<String, Element> row : selectedRows.entrySet()) {
        String rowName = row.getKey();
        Element column = row.getValue().children().get(col);
        quarterlyNumbers.put(rowName, DoubleUtil.parse(column.text()));
      }
      results.put(yearMonthInt, quarterlyNumbers);
    }
    return results;
  }
}
