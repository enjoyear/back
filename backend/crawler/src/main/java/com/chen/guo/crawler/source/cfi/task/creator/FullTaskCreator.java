package com.chen.guo.crawler.source.cfi.task.creator;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.source.cfi.task.CfiNetIncomeTaskLatest;
import com.chen.guo.crawler.util.WebAccessor;

public class FullTaskCreator implements TaskCreator<Integer, Double> {
  public ScrapingTask<Integer, Double> createTask(StockWebPage page, WebAccessor accessor) {
    //return new CfiNetIncomeTaskHist(2013, page, accessor);
    return new CfiNetIncomeTaskLatest(page, accessor);
  }
}
