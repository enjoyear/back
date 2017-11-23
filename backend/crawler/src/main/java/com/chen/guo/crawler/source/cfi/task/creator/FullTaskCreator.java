package com.chen.guo.crawler.source.cfi.task.creator;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.cfi.task.QuarterlyMetricsTask;
import com.chen.guo.crawler.source.cfi.task.factory.QuarterlyMetricsTaskFactory;
import com.chen.guo.crawler.util.WebAccessor;

import java.util.HashSet;
import java.util.Set;

public class FullTaskCreator implements TaskCreator {



  public QuarterlyMetricsTask createTask(StockWebPage page, WebAccessor accessor) {

    Set<String> rowNames = new HashSet<>();
//    rowNames.add("一、营业总收入");
//    rowNames.add("归属于母公司所有者的净利润");
//    rowNames.add("稀释每股收益");
    rowNames.add("归属母公司净利润（元）");
    rowNames.add("总资产周转率（次）");

    return new QuarterlyMetricsTaskFactory(accessor).financialAnalysisIndicatorsTask(page, rowNames);
  }


}
