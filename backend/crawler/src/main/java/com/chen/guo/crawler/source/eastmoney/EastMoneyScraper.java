package com.chen.guo.crawler.source.eastmoney;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.Scraper;
import com.chen.guo.crawler.source.cfi.task.collector.ResultCollector;
import com.chen.guo.crawler.source.cfi.task.creator.TaskCreator;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EastMoneyScraper implements Scraper {
  private static final Logger LOGGER = Logger.getLogger(EastMoneyScraper.class);
  private static final Pattern PATTERN = Pattern.compile("(.*?)\\((.*?)\\)");

  @Override
  public List<StockWebPage> getProfilePages() throws IOException {
    String url = "http://quote.eastmoney.com/stock_list.html";
    Document listPage = WEB_ACCESSOR.connect(url);
    Element content = listPage.getElementById("quotesearch");
    Elements lists = content.getElementsByTag("ul");

    List<StockWebPage> interestedPages = new ArrayList<>(4000);
    for (Element market : lists) {
      for (Element stock : market.children()) {
        Element item = stock.child(0);
        Matcher matcher = PATTERN.matcher(item.text());
        if (!matcher.matches()) {
          throw new RuntimeException("Unable to match pattern for " + item.toString());
        }
        StockWebPage page = new StockWebPage(matcher.group(1), matcher.group(2), item.absUrl("href"));
        if (Scraper.checkForSWPCriteria(page)) {
          interestedPages.add(page);
          System.out.println(page);
        } else {
          //LOGGER.debug("Non-included stock: " + page);
        }
      }
    }

    System.out.println(interestedPages.size()); //3439
    return interestedPages;
  }

  @Override
  public void doScraping(List<StockWebPage> pages, TaskCreator taskCreator, ResultCollector collector) throws Exception {
    throw new UnsupportedOperationException();
  }

  public static void main(String[] args) throws IOException {
    new EastMoneyScraper().getProfilePages();
  }
}
