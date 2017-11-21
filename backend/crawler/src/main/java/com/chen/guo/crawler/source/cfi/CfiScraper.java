package com.chen.guo.crawler.source.cfi;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.Scraper;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.util.WebAccessUtil;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CfiScraper implements Scraper {
  private static final Logger LOGGER = Logger.getLogger(CfiScraper.class);
  private static final Pattern ALL_DIGITS = Pattern.compile("\\d{6}");
  private static final Integer MAX_THREAD_COUNT = 8; //Should be configured to use the max count of cores of the machine
  private static final int MAX_RETRY_ROUNDS = 3;
  private static final int ROUND_TIME_OUT = 60;

  private final Retryer<Void> _retryer = RetryerBuilder.<Void>newBuilder().retryIfExceptionOfType(IOException.class)
      .withStopStrategy(StopStrategies.stopAfterAttempt(5))
      .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS)).build();

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
    Document listPage = WebAccessUtil.getInstance().connect(listUrl);
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
  public void doScraping(List<StockWebPage> pages, ScrapingTask scrapingTask) throws InterruptedException {
    Queue<StockWebPage> jobs = new ArrayDeque<>(pages);
    long startTime = System.currentTimeMillis();
    int r = 0; //indicates current round.

    while (r <= MAX_RETRY_ROUNDS) {
      WebAccessUtil webUtil = WebAccessUtil.getInstance();
      if (r > 0) {
        webUtil = new WebAccessUtil(10 * r);
      }

      ConcurrentLinkedQueue<StockWebPage> failed = new ConcurrentLinkedQueue<>();
      ExecutorService es = Executors.newFixedThreadPool(MAX_THREAD_COUNT);

      while (!jobs.isEmpty()) {
        StockWebPage job = jobs.poll();
        es.submit(new Runnable() {
          @Override
          public void run() {
            try {
              scrapingTask.scrape(job);
              LOGGER.info("Finished " + job);
            } catch (IOException e) {
              failed.add(job);
            }
          }
        });
      }

      LOGGER.info(String.format("Submitted all jobs at round %d.", r));

      es.shutdown(); //stop accepting new requests
      boolean terminated = es.awaitTermination(ROUND_TIME_OUT, TimeUnit.MINUTES);
      if (!terminated) {
        es.shutdownNow();
        throw new RuntimeException(String.format("Timed out while doing Cfi scraping at round %d.", r));
      }

      if (failed.isEmpty()) {
        break;
      }

      LOGGER.info(String.format("%d out of %d pages failed. Will retry(current %d) with longer connection timeout...",
          failed.size(), jobs.size(), r));
      jobs = new ArrayDeque<>(failed);
      ++r;
    }

    LOGGER.info("Whole cfi scraping process took " + (System.currentTimeMillis() - startTime) / 1000 + " seconds to finish");
    if (!jobs.isEmpty()) {
      throw new RuntimeException(String.format(
          "Cfi scraping failed after %d retries for the following pages\n%s", MAX_RETRY_ROUNDS, printFailedPagesList(jobs)));
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
}
