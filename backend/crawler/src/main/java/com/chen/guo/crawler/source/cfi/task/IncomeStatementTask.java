package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

public abstract class IncomeStatementTask implements QuarterlyBasedTask {
  //Used for searching.
  public final static String MENU_ID = "nodea11";
  //Used for validation, could be used for searching as well. It's more complete than ROW_ID.
  public final static String MENU_NAME = "利润分配表";

  protected final StockWebPage _page;
  protected final WebAccessor _accessor;
  protected final Set<String> _rowNames;

  private final CfiMenuClickTask _menuTask;

  protected IncomeStatementTask(StockWebPage page, WebAccessor accessor, Set<String> rowNames) {
    _page = page;
    _accessor = accessor;
    _rowNames = Collections.unmodifiableSet(rowNames);
    _menuTask = new CfiMenuClickTask(MENU_ID, MENU_NAME, accessor);
  }

  @Override
  public String navigate() throws IOException {
    return _menuTask.getPage(_page);
  }

  /**
   * Get the page element for the table body of the main content
   *
   * @param page the page where the table body will be fetched
   */
  protected Element getMainTable(String page) throws IOException {
    Document doc = _accessor.connect(page);
    Element content = doc.getElementById("content");
    return content.getElementsByTag("table").first().getElementsByTag("tbody").first();
  }

  @Override
  public StockWebPage getPage() {
    return _page;
  }

  @Override
  public String getMenuName() {
    return MENU_NAME;
  }
}
