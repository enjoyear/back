package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessor;

import java.util.Set;

public abstract class IncomeStatementTask extends QuarterlyBasedTask {
  public IncomeStatementTask(StockWebPage page, WebAccessor accessor, Set<String> wantedRows) {
    super(page, accessor, wantedRows, "nodea11", "利润分配表");
  }
}
