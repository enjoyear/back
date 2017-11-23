package com.chen.guo.crawler.source.cfi.task.collector;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.cfi.task.unifier.ResultsUnifier;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ResultCollector {
  public Map<StockWebPage, TreeMap<Integer, Map<String, Double>>> results = new HashMap<>();
  private ResultsUnifier _unifier = new ResultsUnifier();

  public void collect(StockWebPage page, TreeMap<Integer, Map<String, Double>> result) {
    results.put(page, _unifier.unify(result));
  }

  public void print() {
    for (Map.Entry<StockWebPage, TreeMap<Integer, Map<String, Double>>> result : results.entrySet()) {
      System.out.println(result.getKey() + ": ");
      for (Map.Entry<Integer, Map<String, Double>> numbers : result.getValue().entrySet()) {
        System.out.println(numbers.getKey() + ": " + numbers.getValue());
      }
    }
  }
}
