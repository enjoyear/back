package com.chen.guo.crawler.source.cfi.task.creator;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.cfi.task.QuarterlyMetricsTask;
import com.chen.guo.crawler.source.cfi.task.factory.QuarterlyMetricsTaskFactory;
import com.chen.guo.crawler.util.WebAccessor;

import java.util.HashSet;
import java.util.Set;

public class FullTaskCreator implements TaskCreator {
  private QuarterlyMetricsTaskFactory _quarterlyTaskFactory;

  public FullTaskCreator(WebAccessor webAccessor) {
    updateWebAccessor(webAccessor);
  }

  public void updateWebAccessor(WebAccessor webAccessor) {
    _quarterlyTaskFactory = new QuarterlyMetricsTaskFactory(webAccessor);
  }

  public QuarterlyMetricsTask createTask(StockWebPage page) {

    Set<String> rowNames = new HashSet<>();
//    rowNames.add("一、营业总收入");
//    rowNames.add("归属于母公司所有者的净利润");
//    rowNames.add("稀释每股收益");
    rowNames.add("归属母公司净利润（元）");

    return _quarterlyTaskFactory.incomeStatementHist2(2013, page, rowNames);
  }

}
