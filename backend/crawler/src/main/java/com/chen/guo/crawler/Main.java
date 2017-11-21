package com.chen.guo.crawler;

import com.chen.guo.crawler.source.Scraper;
import com.chen.guo.crawler.source.cfi.CfiScraper;
import com.chen.guo.crawler.source.cfi.task.CfiNetIncomeTaskHist;

public class Main {
  public static void main(String[] args) throws Exception {
//    ConfigList jobs = CrawlerConfig.getConfig().getList("job");
//    ConfigObject firstJob = (ConfigObject) jobs.get(0);
//    String scraperClass = (String) firstJob.get("scraper").unwrapped();
//    String taskClass = (String) firstJob.get("task").unwrapped();
//
//    Scraper scraper = (Scraper) Class.forName(scraperClass).newInstance();
//
//    //TODO: Refactor this block: move constructor arguments to config objects.
//    ScrapingTask task = null;
//    if (taskClass.equals("com.chen.guo.crawler.source.cfi.task.CfiNetIncomeTaskHist")) {
//      task = new CfiNetIncomeTaskHist(CrawlerConfigUtil.getStartingYear());
//    } else if (taskClass.equals("")) {
//      task = new CfiNetIncomeTaskLatest();
//    }
    // ======== Refactoring END ========

    Scraper scraper = new CfiScraper();
    CfiNetIncomeTaskHist task = new CfiNetIncomeTaskHist(2013);
    //scraper.doScraping(Arrays.asList(new StockWebPage("捷成股份", "300182", "http://quote.cfi.cn/300182.html")), task);
    scraper.doAllScraping(task);
    //System.out.println(task.getTaskResults());
  }
}
