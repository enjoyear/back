package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessor;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public abstract class QuarterlyMetricsTask implements CfiScrapingTask {
  final StockWebPage _page;
  final WebAccessor _accessor;
  final Set<String> _wantedRows;
  CfiMenuNavigator _navigator;

  /**
   * @param wantedRows provide a set of the metrics you are interested in
   */
  QuarterlyMetricsTask(StockWebPage page, WebAccessor accessor, Set<String> wantedRows,
                       CfiMenuNavigator navigator) {
    _page = page;
    _accessor = accessor;
    _wantedRows = Collections.unmodifiableSet(wantedRows);
    _navigator = navigator;
  }

  abstract TreeMap<Integer, Map<String, Double>> scrape(String menuPage) throws IOException;

  @Override
  public TreeMap<Integer, Map<String, Double>> scrape() throws IOException {
    String url = navigate();
    return scrape(url);
  }

  @Override
  public String navigate() throws IOException {
    return _navigator.navigate(_page);
  }

  /**
   * First get all rows from the main table, then only select the wanted rows
   *
   * @return a hashmap: RowName => Row Columns
   */
  HashMap<String, Elements> getSelectedRows(Element table) {
    Set<String> rowsWanted = new HashSet<>(_wantedRows);
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
    return selectedRows;
  }

  /**
   * Validate the header to make sure that the table format doesn't change
   */
  Elements validateHeader(String menuPage, Element table) {
    Element yearMonthTr = table.getElementsByTag("tr").get(1);
    Elements headerRow = yearMonthTr.children();
    if (!headerRow.first().ownText().equals("截止日期")) {
      throw new RuntimeException(String.format("Header validation failed: Expected the header line 截止日期, but getting %s for the url %s",
          headerRow.first().ownText(), menuPage));
    }
    return headerRow;
  }

  @Override
  public StockWebPage getPage() {
    return _page;
  }

  @Override
  public CfiMenuNavigator getNavigator() {
    return _navigator;
  }

  @Override
  public WebAccessor getWebAccessor() {
    return _accessor;
  }
}
