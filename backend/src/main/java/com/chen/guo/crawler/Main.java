package com.chen.guo.crawler;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.Scraper;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.source.cfi.task.CfiScrapingCapitalizationTaskHist;
import com.chen.guo.crawler.source.cfi.task.CfiScrapingNetIncomeTaskHist;
import com.chen.guo.crawler.source.cfi.task.CfiScrapingNetIncomeTaskLatest;
import com.chen.guo.crawler.util.CrawlerConfigUtil;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigObject;

import java.net.ConnectException;
import java.util.Arrays;

public class Main {
  public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, ConnectException {
    ConfigList jobs = CrawlerConfig.getConfig().getList("job");
    ConfigObject firstJob = (ConfigObject) jobs.get(0);
    String scraperClass = (String) firstJob.get("scraper").unwrapped();
    String taskClass = (String) firstJob.get("task").unwrapped();

    Scraper scraper = (Scraper) Class.forName(scraperClass).newInstance();

    //TODO: Refactor this block: move constructor arguments to config objects.
    ScrapingTask task = null;
    if (taskClass.equals("com.chen.guo.crawler.source.cfi.task.CfiScrapingNetIncomeTaskHist")) {
      task = new CfiScrapingNetIncomeTaskHist(CrawlerConfigUtil.getStartingYear());
    } else if (taskClass.equals("")) {
      task = new CfiScrapingNetIncomeTaskLatest();
    }
    // ======== Refactoring END ========

    CfiScrapingCapitalizationTaskHist scrapingTask = new CfiScrapingCapitalizationTaskHist(2013);
    scraper.doScraping(Arrays.asList(new StockWebPage("捷成股份", "300182", "http://quote.cfi.cn/300182.html")), scrapingTask);

    System.out.println(scrapingTask.getTaskResults());
  }
}
