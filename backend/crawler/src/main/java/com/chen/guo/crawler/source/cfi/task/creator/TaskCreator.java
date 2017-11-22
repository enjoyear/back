package com.chen.guo.crawler.source.cfi.task.creator;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.source.cfi.task.QuarterlyBasedTask;
import com.chen.guo.crawler.util.WebAccessor;

public interface TaskCreator {
  QuarterlyBasedTask createTask(StockWebPage page, WebAccessor accessor);
}
