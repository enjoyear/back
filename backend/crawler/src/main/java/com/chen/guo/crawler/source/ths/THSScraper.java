package com.chen.guo.crawler.source.ths;

import com.chen.guo.common.date.DateTimeUtil;
import com.chen.guo.common.number.DoubleUtil;
import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.source.Scraper;
import com.chen.guo.crawler.source.ScrapingTask;
import com.chen.guo.crawler.source.cfi.task.collector.ResultCollector;
import com.chen.guo.crawler.source.cfi.task.creator.TaskCreator;
import com.chen.guo.crawler.source.eastmoney.EastMoneyScraper;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.*;

public class THSScraper implements Scraper {
  public final static String URL_PATTERN = "http://stockpage.10jqka.com.cn/%s/";

  @Override
  public List<StockWebPage> getProfilePages() throws IOException {
    return new EastMoneyScraper().getProfilePages();
  }

  @Override
  public void doScraping(List<StockWebPage> pages, TaskCreator taskCreator, ResultCollector collector) throws InterruptedException {
    Set<String> wantedMetrics = new HashSet<>();
    wantedMetrics.add("净利润");
    wantedMetrics.add("营业总收入");

    for (StockWebPage page : pages) {
      Document doc = null;
      try {
        doc = WEB_ACCESSOR.connect(String.format(URL_PATTERN, page.getCode()));
      } catch (IOException e) {
        e.printStackTrace();
      }

      Element statementsUrl = doc.getElementsContainingOwnText("财务指标").first();
      String menuPage = statementsUrl.absUrl("href");
      try {
        Document connect = WEB_ACCESSOR.connect(menuPage);
        Element dataIframe = connect.getElementById("dataifm");
        String detailsUrl = dataIframe.attr("src");

        try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
          WebClientOptions options = webClient.getOptions();
          options.setCssEnabled(false);
          options.setJavaScriptEnabled(false);
          HtmlPage detailsPage = webClient.getPage(detailsUrl);
          //Element cwzbTable = detailsPage.getElementById("cwzbDemo");
          //Element left_thead = cwzbTable.getElementsByClass("left_thead").first();
          //Element data_tbody = cwzbTable.getElementsByClass("data_tbody").first();

          DomElement cwzbTable = detailsPage.getElementById("main");
          /**
           * An alternative way would be get the json file directly
           * Source code can be found in the js file. finance_v12.20170914.js
           * $.ajax({
           type: 'GET',
           url: 'http://basic.10jqka.com.cn/api/stock/finance/' + $("#stockCode").val() + '_' + reportType + '.json',
           success: function(data) {...}
           })
           * try "curl -XGET http://basic.10jqka.com.cn/api/stock/finance/300181_main.json"
           "
           */
          String dataJson = cwzbTable.getTextContent();

          //JSONParser parser = new JsonParser();
          JsonParser parser = new JsonParser();
          // https://www.branah.com/unicode-converter
          // UTF-16 => Unicode text
          JsonObject jsonObj = parser.parse(dataJson).getAsJsonObject();
          JsonArray leftTitle = jsonObj.get("title").getAsJsonArray();
          JsonArray byReport = jsonObj.get("report").getAsJsonArray();
          JsonArray byYear = jsonObj.get("year").getAsJsonArray();
          JsonArray byQuarter = jsonObj.get("simple").getAsJsonArray();

          HashMap<String, JsonArray> wantedArrays = new HashMap<>();
          //Skip the first line, which is "科目\年度"
          for (int i = 1; i < leftTitle.size(); ++i) {
            JsonArray titleArray = leftTitle.get(i).getAsJsonArray();
            String currentTitle = titleArray.get(0).getAsString();
            if (wantedMetrics.contains(currentTitle)) {
              wantedArrays.put(currentTitle, byReport.get(i).getAsJsonArray());
            }
          }

          JsonArray dateArray = byReport.get(0).getAsJsonArray();
          TreeMap<Integer, Map<String, Double>> results = new TreeMap<>();
          for (int c = 0; c < byReport.size(); ++c) {
            String date = dateArray.get(c).getAsString();
            Integer key = DateTimeUtil.getDateInt(date, ScrapingTask.DEFAULT_DATE_PATTERN);
            Map<String, Double> val = new HashMap<>();
            for (Map.Entry<String, JsonArray> wanted : wantedArrays.entrySet()) {
              val.put(wanted.getKey(), DoubleUtil.parse(wanted.getValue().get(c).getAsString()));
            }
            results.put(key, val);
          }

          for (Map.Entry<Integer, Map<String, Double>> result : results.entrySet()) {
            System.out.println(result.getKey() + ": " + result.getValue());
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) throws Exception {
    Scraper scraper = new THSScraper();
    scraper.doScraping(Arrays.asList(new StockWebPage("捷成股份", "300182", "http://quote.cfi.cn/300182.html")),
        null, null);
  }
}
