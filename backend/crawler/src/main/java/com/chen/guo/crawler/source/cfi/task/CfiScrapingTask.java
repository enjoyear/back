package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public interface CfiScrapingTask {
  String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

  /**
   * Navigate to the menu
   *
   * @return the URL after navigation
   * @throws IOException
   */
  String navigate() throws IOException;

  TreeMap<Integer, Map<String, Double>> scrape(String menuPage) throws IOException;

  StockWebPage getPage();

  String getMenuName();

  WebAccessor getWebAccessor();

  default TreeMap<Integer, Map<String, Double>> scrape() throws IOException {
    String url = navigate();
    return scrape(url);
  }

  /**
   * Get the page element for the table body of the main content
   *
   * @param page the page where the table body will be fetched
   */
  default Element connectAndGetContentTable(String page) throws IOException {
    Document pageDoc = getWebAccessor().connect(page);
    return getContentTable(pageDoc);
  }

  default Element getContentTable(Document pageDoc) {
    Element content = pageDoc.getElementById("content");
    return content.getElementsByTag("table").first().getElementsByTag("tbody").first();
  }
}
