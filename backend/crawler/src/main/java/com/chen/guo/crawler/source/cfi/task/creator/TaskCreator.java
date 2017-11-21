package com.chen.guo.crawler.source.cfi.task.creator;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.util.WebAccessor;

public interface TaskCreator<T1, T2> {
  ScrapingTask<T1, T2> createTask(StockWebPage page, WebAccessor accessor);
}
