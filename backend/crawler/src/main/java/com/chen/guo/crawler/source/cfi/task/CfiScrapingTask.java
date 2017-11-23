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

  StockWebPage getPage();

  WebAccessor getWebAccessor();

  TreeMap<Integer, Map<String, Double>> scrape() throws IOException;

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
