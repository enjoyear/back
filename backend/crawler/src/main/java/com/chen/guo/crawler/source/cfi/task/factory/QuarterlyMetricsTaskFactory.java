package com.chen.guo.crawler.source.cfi.task.factory;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.cfi.task.*;
import com.chen.guo.crawler.util.WebAccessor;

import java.util.Set;

public class QuarterlyMetricsTaskFactory {
  private final WebAccessor _webAccessor;
  private final CfiMenuNavigator _incomeStatementNavigator;
  private final CfiMenuClickTask _financialAnalysisIndicatorsNavigator;

  public QuarterlyMetricsTaskFactory(WebAccessor webAccessor) {
    _webAccessor = webAccessor;
    _incomeStatementNavigator = new CfiMenuClickTask("nodea11", "利润分配表", webAccessor);
    _financialAnalysisIndicatorsNavigator = new CfiMenuClickTask("nodea1", "财务分析指标", webAccessor);
  }

  public QuarterlyMetricsTask incomeStatementLatest(StockWebPage page, Set<String> rowNames) {
    return new QuarterlyMetricsTaskLatest(page, _webAccessor, rowNames, _incomeStatementNavigator);
  }

  public QuarterlyMetricsTask incomeStatementHist(int startYear, StockWebPage page, Set<String> rowNames) {
    return new QuarterlyMetricsTaskHist(startYear, page, _webAccessor, rowNames, _incomeStatementNavigator);
  }

  public QuarterlyMetricsTask incomeStatementHist2(int startYear, StockWebPage page, Set<String> rowNames) {
    return new QuarterlyMetricsTaskHist3(startYear, page, _webAccessor, rowNames, _incomeStatementNavigator);
  }

  public QuarterlyMetricsTask financialAnalysisIndicatorsLatest(StockWebPage page, Set<String> rowNames) {
    return new QuarterlyMetricsTaskLatest(page, _webAccessor, rowNames, _financialAnalysisIndicatorsNavigator);
  }

  public QuarterlyMetricsTask financialAnalysisIndicatorsHist(int startYear, StockWebPage page, Set<String> rowNames) {
    return new QuarterlyMetricsTaskHist(startYear, page, _webAccessor, rowNames, _financialAnalysisIndicatorsNavigator);
  }
}
