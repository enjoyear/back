package com.chen.guo.crawler.source.cfi.task.factory;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.cfi.task.QuarterlyMetricsTask;
import com.chen.guo.crawler.source.cfi.task.QuarterlyMetricsTaskHist;
import com.chen.guo.crawler.source.cfi.task.QuarterlyMetricsTaskHist2;
import com.chen.guo.crawler.source.cfi.task.QuarterlyMetricsTaskLatest;
import com.chen.guo.crawler.util.WebAccessor;

import java.util.Set;

public class QuarterlyMetricsTaskFactory {
  private final WebAccessor _webAccessor;

  public QuarterlyMetricsTaskFactory(WebAccessor webAccessor) {
    _webAccessor = webAccessor;
  }

  public QuarterlyMetricsTask incomeStatementLatest(StockWebPage page, Set<String> rowNames) {
    return new QuarterlyMetricsTaskLatest(page, _webAccessor, rowNames, "nodea11", "利润分配表");
  }

  public QuarterlyMetricsTask incomeStatementHist(int startYear, StockWebPage page, Set<String> rowNames) {
    return new QuarterlyMetricsTaskHist(startYear, page, _webAccessor, rowNames, "nodea11", "利润分配表");
  }

  public QuarterlyMetricsTask incomeStatementHist2(int startYear, StockWebPage page, Set<String> rowNames) {
    return new QuarterlyMetricsTaskHist2(startYear, page, _webAccessor, rowNames, "nodea11", "利润分配表");
  }

  public QuarterlyMetricsTask financialAnalysisIndicatorsLatest(StockWebPage page, Set<String> rowNames) {
    return new QuarterlyMetricsTaskLatest(page, _webAccessor, rowNames, "nodea1", "财务分析指标");
  }

  public QuarterlyMetricsTask financialAnalysisIndicatorsHist(int startYear, StockWebPage page, Set<String> rowNames) {
    return new QuarterlyMetricsTaskHist(startYear, page, _webAccessor, rowNames, "nodea1", "财务分析指标");
  }
}
