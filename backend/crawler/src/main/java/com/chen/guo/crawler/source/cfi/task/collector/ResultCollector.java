package com.chen.guo.crawler.source.cfi.task.collector;

import com.chen.guo.crawler.model.StockWebPage;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ResultCollector {
  public Map<StockWebPage, TreeMap<Integer, Double>> results = new HashMap<StockWebPage, TreeMap<Integer, Double>>();

  public void collect(StockWebPage page, TreeMap<Integer, Double> result) {
    results.put(page, result);
  }

  public void print() {
    for (Map.Entry<StockWebPage, TreeMap<Integer, Double>> result : results.entrySet()) {
      System.out.println(result.getKey() + ": " + result.getValue());
    }
  }
}
