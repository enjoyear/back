package com.chen.guo.crawler.source;

import com.chen.guo.crawler.model.StockWebPage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

public abstract class ScrapingTask<TKey, TValue> {
  public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(("yyyy-MM-dd"));

  public abstract TreeMap<TKey, TValue> scrape() throws IOException;

  public abstract StockWebPage getPage();

  protected static LocalDate getDate(String dateString) {
    return LocalDate.parse(dateString, formatter);
  }
}
