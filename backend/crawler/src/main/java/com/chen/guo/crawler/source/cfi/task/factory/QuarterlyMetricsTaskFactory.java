package com.chen.guo.crawler.source.cfi.task.factory;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.cfi.task.QuarterlyMetricsTaskLatest;
import com.chen.guo.crawler.source.cfi.task.QuarterlyMetricsTask;
import com.chen.guo.crawler.util.WebAccessor;

import java.util.Set;

public class QuarterlyMetricsTaskFactory {
  private final WebAccessor _webAccessor;

  public QuarterlyMetricsTaskFactory(WebAccessor webAccessor) {
    _webAccessor = webAccessor;
  }

  public QuarterlyMetricsTask incomeStatementTask(StockWebPage page, Set<String> rowNames) {
    return new QuarterlyMetricsTaskLatest(page, _webAccessor, rowNames, "nodea11", "利润分配表");
  }

  public QuarterlyMetricsTask financialAnalysisIndicatorsTask(StockWebPage page, Set<String> rowNames) {
    return new QuarterlyMetricsTaskLatest(page, _webAccessor, rowNames, "nodea1", "财务分析指标");
  }
}
