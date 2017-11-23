package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessor;

import java.util.Set;

public abstract class FinancialAnalysisIndicatorsTask extends QuarterlyBasedTask {
  public FinancialAnalysisIndicatorsTask(StockWebPage page, WebAccessor accessor, Set<String> wantedRows) {
    super(page, accessor, wantedRows, "nodea1", "财务分析指标");
  }
}
