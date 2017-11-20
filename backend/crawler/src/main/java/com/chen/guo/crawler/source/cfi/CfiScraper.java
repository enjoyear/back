package com.chen.guo.crawler.source.cfi;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.Scraper;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.source.cfi.task.CfiNetIncomeTaskHist;
import com.chen.guo.crawler.util.WebAccessUtil;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.ConnectException;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CfiScraper implements Scraper {
  private static final Logger LOGGER = Logger.getLogger(CfiScraper.class);
  private static final Pattern ALL_DIGITS = Pattern.compile("\\d{6}");
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
            LOGGER.error(String.format("Unable to get all pages from the seed '%s' due to %s. Retrying...", seedUrl, e.getMessage()));
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
    LOGGER.info("Total numbers of StockWebPage created: " + syncAll.size()); //3356
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

        if (checkForSWPCriteria(sp)) {
          //LOGGER.info(sp.toString());
          interestedPages.add(sp);
        } else {
          LOGGER.debug("Non-included stock: " + sp);
        }
      }
    }
    return interestedPages;
  }

  @Override
  public void doScraping(List<StockWebPage> pages, ScrapingTask scrapingTask) throws ConnectException {
    int retryCount = 3;
    List<StockWebPage> jobs = pages;
    long startTime = System.currentTimeMillis();

    ForkJoinPool pool = new ForkJoinPool(MAX_THREAD_COUNT);
    WebAccessUtil localWebUtil = WEB_PAGE_UTIL;
    WebAccessUtil webUtil20 = new WebAccessUtil(20);

    while (!jobs.isEmpty() && retryCount > 0) {
      ConcurrentLinkedQueue<StockWebPage> failed = new ConcurrentLinkedQueue<>();
      pool.invoke(new CfiScrapingAsyncAction(scrapingTask, jobs, failed, localWebUtil));
      LOGGER.info(
          String.format("%d out of %d pages failed. Retrying(%d) with longer connection timeout...",
              failed.size(), jobs.size(), retryCount));

      jobs = new ArrayList<>();
      if (!failed.isEmpty()) {
        --retryCount;
        localWebUtil = webUtil20; //set longer connection time
        LOGGER.warn(printFailedPagesList(failed));
        jobs.addAll(failed);
      }
    }

    LOGGER.info("Whole process took " + (System.currentTimeMillis() - startTime) / 1000 + " seconds to finish");

    if (!jobs.isEmpty() && retryCount == 0) {
      throw new ConnectException(printFailedPagesList(jobs));
    }
  }

  private String printFailedPagesList(Collection<StockWebPage> failed) {
    return String.format("Failed to download %d pages:\n%s", failed.size(),
        String.join(",", failed.stream().map(StockWebPage::toString).collect(Collectors.toList())));
  }

  /**
   * Exclude all ST, *ST, s*st
   */
  static boolean checkForSWPCriteria(StockWebPage sp) {
    String code = sp.getCode();
    if (code.startsWith("0") ||
        code.startsWith("6") ||
        (code.startsWith("3") && !code.startsWith("39"))) {
      if (checkForCodeCriteria(code)) {
        //Exclude when the name is too long
        if (sp.getName().length() > 4 &&
            !sp.getName().equals("TCL集团")) {
          if (!sp.getName().toLowerCase().startsWith("*st"))
            LOGGER.info("Exclude: " + sp);
          return false;
        }

        //Exclude ST
        if (sp.getName().toLowerCase().startsWith("st")) {
          return false;
        }

        return true;
      }
    }
    return false;
  }

  /**
   * Make sure a 6-digit code
   */
  public static boolean checkForCodeCriteria(String str) {
    return ALL_DIGITS.matcher(str).matches();
  }

  public static void main(String[] args) throws Exception {
    new CfiScraper().getProfilePages();
  }
}
