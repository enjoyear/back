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
  private CfiMenuClickTask _financialAnalysisIndicatorsNavigator;
  private CfiMenuNavigator _incomeStatementNavigator;
  private CfiMenuClickTask _capitalStructureNavigator;

  public FullTaskCreator(WebAccessor webAccessor) {
    updateWebAccessor(webAccessor);
  }

  public void updateWebAccessor(WebAccessor webAccessor) {
    _webAccessor = webAccessor;
    _financialAnalysisIndicatorsNavigator = new CfiMenuClickTask("nodea1", "财务分析指标", webAccessor);
    _incomeStatementNavigator = new CfiMenuClickTask("nodea11", "利润分配表", webAccessor);
    _capitalStructureNavigator = new CfiMenuClickTask("nodea21", "股本结构", webAccessor);
  }

  public CfiScrapingTask createTask(StockWebPage page) {
    int startYear = 2014;

    Set<String> incomeStatementRows = new HashSet<>();
    incomeStatementRows.add("一、营业总收入");
    DatebasedMetricsTaskHistType2 incomeStatementTask = new DatebasedMetricsTaskHistType2(startYear, page, _webAccessor, incomeStatementRows, _incomeStatementNavigator);

    Set<String> faiRows = new HashSet<>();
    faiRows.add("归属母公司净利润（元）");
    DatebasedMetricsTaskHistType1 faiTask = new DatebasedMetricsTaskHistType1(startYear, page, _webAccessor, faiRows, _financialAnalysisIndicatorsNavigator);

    Set<String> capitalStructureRows = new HashSet<>();
    capitalStructureRows.add("1.A股(股)");
    DatebasedMetricsTaskHistType2 capitalStructureTask = new DatebasedMetricsTaskHistType2(startYear, page, _webAccessor, capitalStructureRows, _capitalStructureNavigator);


    List<CfiScrapingTask> tasks = new ArrayList<>();
    tasks.add(incomeStatementTask);
    tasks.add(faiTask);
    tasks.add(capitalStructureTask);

    return new CompositeCfiScrapingTask(page, tasks);
  }
}
