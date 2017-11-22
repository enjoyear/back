package com.chen.guo.crawler.source.cfi.task.creator;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.source.cfi.task.CfiNetIncomeTaskHist;
import com.chen.guo.crawler.source.cfi.task.CfiNetIncomeTaskLatest;
import com.chen.guo.crawler.source.cfi.task.IncomeStatementTaskLatest;
import com.chen.guo.crawler.source.cfi.task.QuarterlyBasedTask;
import com.chen.guo.crawler.util.WebAccessor;

import java.util.HashSet;
import java.util.Set;

public class FullTaskCreator implements TaskCreator {
  public QuarterlyBasedTask createTask(StockWebPage page, WebAccessor accessor) {

    Set<String> rowNames = new HashSet<>();
    rowNames.add("一、营业总收入");
    rowNames.add("归属于母公司所有者的净利润");

    return new IncomeStatementTaskLatest(page, accessor, rowNames);
  }
}
