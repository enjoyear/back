package com.chen.guo.crawler.source.cfi.task.creator;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.cfi.task.*;
import com.chen.guo.crawler.util.WebAccessor;

import java.util.HashSet;
import java.util.Set;

public class FullTaskCreator implements TaskCreator {
  private WebAccessor _webAccessor;
  private CfiMenuNavigator _incomeStatementNavigator;
  private CfiMenuClickTask _financialAnalysisIndicatorsNavigator;

  public FullTaskCreator(WebAccessor webAccessor) {
    updateWebAccessor(webAccessor);
  }

  public void updateWebAccessor(WebAccessor webAccessor) {
    _webAccessor = webAccessor;
    _incomeStatementNavigator = new CfiMenuClickTask("nodea11", "利润分配表", webAccessor);
    _financialAnalysisIndicatorsNavigator = new CfiMenuClickTask("nodea1", "财务分析指标", webAccessor);
  }

  public CfiScrapingTask createTask(StockWebPage page) {

    Set<String> rowNames = new HashSet<>();
//    rowNames.add("一、营业总收入");
//    rowNames.add("归属于母公司所有者的净利润");
//    rowNames.add("稀释每股收益");
    rowNames.add("一、营业总收入");

    return new QuarterlyMetricsTaskHistType2(2014, page, _webAccessor, rowNames, _incomeStatementNavigator);
  }
}
