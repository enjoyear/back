package com.chen.guo.crawler.source.cfi.task.creator;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.cfi.task.*;
import com.chen.guo.crawler.util.WebAccessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    Set<String> incomeStatementRows = new HashSet<>();
    incomeStatementRows.add("一、营业总收入");
    QuarterlyMetricsTaskHistType2 incomeStatementTask = new QuarterlyMetricsTaskHistType2(2014, page, _webAccessor, incomeStatementRows, _incomeStatementNavigator);

    Set<String> faiRows = new HashSet<>();
    faiRows.add("归属母公司净利润（元）");
    QuarterlyMetricsTaskHistType1 faiTask = new QuarterlyMetricsTaskHistType1(2014, page, _webAccessor, faiRows, _financialAnalysisIndicatorsNavigator);

    List<CfiScrapingTask> tasks = new ArrayList<>();
    tasks.add(incomeStatementTask);
    tasks.add(faiTask);

    return new CompositeCfiScrapingTask(page, tasks);
  }
}
