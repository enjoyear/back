package com.chen.guo.crawler.source.cfi.task.collector;

import com.chen.guo.crawler.model.StockWebPage;

import java.util.HashMap;
import java.util.Map;

public class ResultCollector {
  public Map<StockWebPage, Map<Integer, Map<String, Double>>> results = new HashMap<>();

  public void collect(StockWebPage page, Map<Integer, Map<String, Double>> result) {
    results.put(page, result);
  }

  public void print() {
    for (Map.Entry<StockWebPage, Map<Integer, Map<String, Double>>> result : results.entrySet()) {
      System.out.println(result.getKey() + ": ");
      for (Map.Entry<Integer, Map<String, Double>> numbers : result.getValue().entrySet()) {
        System.out.println(numbers.getKey() + ": " + numbers.getValue());
      }
    }
  }
}
