package com.chen.guo.crawler.source;

import com.chen.guo.crawler.model.StockWebPage;

import java.io.IOException;
import java.util.TreeMap;

public abstract class ScrapingTask<TKey, TValue> {
  public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

  public abstract TreeMap<TKey, TValue> scrape() throws IOException;

  public abstract StockWebPage getPage();
}
