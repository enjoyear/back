package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.common.date.DateTimeUtil;
import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class IncomeStatementTaskHist extends IncomeStatementTask {

  private final int _startYear;

  /**
   * @param startYear denotes the oldest year we care about. This startYear is inclusive
   * @param page      the StockWebPage
   * @param accessor
   * @param rowNames  provide a set of rows you want to fetch
   */
  public IncomeStatementTaskHist(int startYear, StockWebPage page, WebAccessor accessor, Set<String> rowNames) {
    super(page, accessor, rowNames);
    _startYear = startYear;
  }

  @Override
  public TreeMap<Integer, Map<String, Double>> scrape(String menuPage) throws IOException {
    Element table = connectAndGetContentTable(menuPage);
    validateHeader(menuPage, table);

    HashMap<String, Element> selectedRows = getSelectedRows(table);
    TreeMap<Integer, Map<String, Double>> results = new TreeMap<>();

    for (Map.Entry<String, Element> rowEntry : selectedRows.entrySet()) {
      String name = rowEntry.getKey();
      Element row = rowEntry.getValue();
      String detailsPageUrl = row.absUrl("href");

      Element detailsTable = connectAndGetContentTable(detailsPageUrl);
      //Get all historical data
      Elements rows = detailsTable.children();

      Element headerRow = rows.get(1);
      //Do validation with the headerRow
      if (!headerRow.child(0).text().equals("报告期") || !headerRow.child(1).text().trim().equals(name)) {
        throw new RuntimeException(
            String.format("Layout of the table seem to be changed for: %s. Expecting header '%s', but actually get '%s'.",
                detailsPageUrl, name, headerRow.child(1).text()));
      }

      //Skip first two lines of headers, and last empty line.
      for (int r = 2; r < rows.size() - 1; ++r) {
        Element detailRow = rows.get(r);
        Elements detailColumns = detailRow.children();
        LocalDate date = DateTimeUtil.getDate(detailColumns.get(0).text(), DEFAULT_DATE_PATTERN);
        if (date.getYear() < _startYear) {
          break;
        }

        int yearMonthKey = date.getYear() * 100 + date.getMonthValue();
        Map<String, Double> data = results.putIfAbsent(yearMonthKey, new HashMap<>());
        data.put(name, Double.valueOf(detailColumns.get(1).text()));
      }
    }
    return results;
  }
}
