package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CompositeCfiScrapingTask implements CfiScrapingTask {

  private final StockWebPage _swp;
  private final List<CfiScrapingTask> _tasks;

  public CompositeCfiScrapingTask(StockWebPage swp, List<CfiScrapingTask> tasks) {
    _swp = swp;
    _tasks = tasks;
  }

  @Override
  public StockWebPage getPage() {
    return _swp;
  }

  @Override
  public WebAccessor getWebAccessor() {
    throw new UnsupportedOperationException();
  }

  @Override
  public TreeMap<Integer, Map<String, Double>> scrape() throws IOException {
    TreeMap<Integer, Map<String, Double>> results = new TreeMap<>();
    for (CfiScrapingTask task : _tasks) {
      merge(results, task.scrape());
    }
    return results;
  }

  /**
   * Merge incremental map into merged map
   */
  static void merge(Map<Integer, Map<String, Double>> merged, Map<Integer, Map<String, Double>> incremental) {
    for (Map.Entry<Integer, Map<String, Double>> increment : incremental.entrySet()) {
      Map<String, Double> map = merged.computeIfAbsent(increment.getKey(), i -> new HashMap<>());
      map.putAll(increment.getValue());
    }
  }
}
