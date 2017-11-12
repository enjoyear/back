package com.chen.guo.crawler.source.cfi;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.Scraper;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.util.WebAccessUtil;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class CfiScraper implements Scraper {
  private static final Logger _logger = Logger.getLogger(CfiScraper.class);
  private static final Integer MAX_THREAD_COUNT = 8; //Should be configured to use the max count of cores of the machine
  private static final WebAccessUtil WEB_PAGE_UTIL = WebAccessUtil.getInstance();

  @Override
  public List<StockWebPage> getProfilePages() {
    String shA = "http://quote.cfi.cn/stockList.aspx?t=11";
    String szA = "http://quote.cfi.cn/stockList.aspx?t=12";
    String szZX = "http://quote.cfi.cn/stockList.aspx?t=13";
    String szCY = "http://quote.cfi.cn/stockList.aspx?t=14";
    List<String> catalogUrls = Arrays.asList(shA, szA, szZX, szCY);

    ExecutorService es = Executors.newCachedThreadPool();
    List<StockWebPage> syncAll = Collections.synchronizedList(new ArrayList<>(16000));

    catalogUrls.forEach(seedUrl -> {
      es.submit(() -> {
        List<StockWebPage> pages = null;
        while (pages == null) {
          try {
            pages = getProfilePages(seedUrl);
          } catch (IOException e) {
            _logger.error(String.format("Unable to get all pages from the seed '%s' due to %s. Retrying...", seedUrl, e.getMessage()));
          }
        }
        syncAll.addAll(pages);
      });
    });
    es.shutdown();
    try {
      es.awaitTermination(20, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    _logger.info("Total numbers of StockWebPage created: " + syncAll.size());
    return new ArrayList<>(syncAll);
  }

  private List<StockWebPage> getProfilePages(String listUrl) throws IOException {
    Document listPage = WEB_PAGE_UTIL.getPageContent(listUrl);
    Element content = listPage.getElementById("divcontent");
    Element table = content.getElementsByTag("table").first().getElementsByTag("tbody").first();
    Elements rows = table.getElementsByTag("tr");

    List<StockWebPage> interestedPages = new ArrayList<>(4000);
    for (Element row : rows) {
      for (Element col : row.children()) {
        String nameCode = col.text();
        int index = nameCode.indexOf("(");
        String code = nameCode.substring(index + 1, nameCode.length() - 1).trim();
        StockWebPage sp = new StockWebPage(
            nameCode.substring(0, index).trim(), code, WebAccessUtil.getHyperlink(col));
        _logger.info(sp.toString());

        if (code.startsWith("0") || code.startsWith("6") || code.startsWith("3")) {
          if (code.length() != 6) {
            _logger.warn(String.format("Unexpected code '%s' at list page %s", sp, listUrl));
            continue;
          }
          interestedPages.add(sp);
        } else {
          _logger.debug("Non-included stock: " + sp);
        }
      }
    }
    return interestedPages;
  }

  @Override
  public void doScraping(List<StockWebPage> pages, ScrapingTask scrapingTask) throws ConnectException {
    long startTime = System.currentTimeMillis();

    ForkJoinPool pool = new ForkJoinPool(MAX_THREAD_COUNT);
    WebAccessUtil localWebUtil = WEB_PAGE_UTIL;
    WebAccessUtil webUtil20 = new WebAccessUtil(20);
    int retryCount = 3;
    List<StockWebPage> workToBeDone = pages;
    while (!workToBeDone.isEmpty() && retryCount > 0) {
      ConcurrentLinkedQueue<StockWebPage> failedPages = new ConcurrentLinkedQueue<>();
      pool.invoke(new CfiScrapingAsyncAction(scrapingTask, workToBeDone, failedPages, localWebUtil));
      _logger.info(String.format("%d out of %d pages failed", failedPages.size(), workToBeDone.size()));

      workToBeDone = new ArrayList<>();
      if (!failedPages.isEmpty()) {
        --retryCount;
        localWebUtil = webUtil20; //set longer connection time
        workToBeDone.clear();
        failedPages.forEach(workToBeDone::add); //Add failed pages to workToBeDone and try again.
        _logger.warn(String.format("Retries remaining %d times. Failed pages are as follows: ", retryCount));
        _logger.warn(String.join(",", workToBeDone.stream().map(StockWebPage::toString).collect(Collectors.toList())));
      }
    }

    _logger.info("Whole process took " + (System.currentTimeMillis() - startTime) / 1000 + " seconds to finish");
    if (!workToBeDone.isEmpty() && retryCount == 0) {
      throw new ConnectException(String.format(
          "Failed to connect to %d pages:\n%s", workToBeDone.size(),
          String.join(",", workToBeDone.stream().map(StockWebPage::toString).collect(Collectors.toList()))));
    }
  }

  public static void main(String[] args) throws Exception {
    new CfiScraper().getProfilePages();
  }
}
