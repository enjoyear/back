package com.chen.guo.crawler.source.cfi.task.creator;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.cfi.task.QuarterlyMetricsTask;
import com.chen.guo.crawler.util.WebAccessor;

public interface TaskCreator {
  QuarterlyMetricsTask createTask(StockWebPage page);

  void updateWebAccessor(WebAccessor webAccessor);
}
