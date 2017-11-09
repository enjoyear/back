package com.chen.guo.crawler.source.cfi;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.util.WebAccessUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveAction;

class CfiScrapingAsyncAction extends RecursiveAction {
  private static final Logger logger = Logger.getLogger(CfiScrapingAsyncAction.class);
  private ConcurrentLinkedQueue<StockWebPage> failedPages;
  private final WebAccessUtil webUtil;
  private final ScrapingTask task;
  private final List<StockWebPage> pages;
  private final int low;
  private final int high;
  private final static int TASK_COUNT_PER_THREAD = 2; //TASK_COUNT_PER_THREAD >= 1

  /**
   * @param pages       Keep all tasks to do. pages must be random accessible.
   * @param failedPages As a return value
   */
  public CfiScrapingAsyncAction(ScrapingTask task, List<StockWebPage> pages,
                                ConcurrentLinkedQueue<StockWebPage> failedPages) {
    this(task, pages, 0, pages.size(), failedPages, WebAccessUtil.getInstance());
  }

  /**
   * @param pages       Keep all tasks to do. pages must be random accessible.
   * @param failedPages As a return value
   */
  public CfiScrapingAsyncAction(ScrapingTask task, List<StockWebPage> pages,
                                ConcurrentLinkedQueue<StockWebPage> failedPages, WebAccessUtil webUtil) {
    this(task, pages, 0, pages.size(), failedPages, webUtil);
  }

  /**
   * @param pages       Keep all tasks to do. pages must be random accessible.
   * @param low         Inclusive low end
   * @param high        Exclusive high end
   * @param failedPages As a return value
   */
  public CfiScrapingAsyncAction(ScrapingTask task, List<StockWebPage> pages, int low, int high,
                                ConcurrentLinkedQueue<StockWebPage> failedPages, WebAccessUtil webUtil) {
    this.task = task;
    this.pages = pages;
    this.low = low;
    this.high = high;
    this.failedPages = failedPages;
    this.webUtil = webUtil;
  }

  @Override
  protected void compute() {
    if (high - low <= TASK_COUNT_PER_THREAD) {
      for (int i = low; i < high; ++i) {
        StockWebPage page = pages.get(i);
        try {
          task.scrape(page);
        } catch (IOException e) {
          failedPages.add(page);
          logger.error("Current URL: " + page.getUrl() + System.lineSeparator() + e.getMessage());
        }
      }
    } else {
      int mid = (low + high) >>> 1;
      //Divide and conquer
      invokeAll(
          new CfiScrapingAsyncAction(task, pages, low, mid, failedPages, webUtil),
          new CfiScrapingAsyncAction(task, pages, mid, high, failedPages, webUtil));
    }
  }
}