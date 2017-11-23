package com.chen.guo.crawler.source.cfi.task;

import com.chen.guo.crawler.model.StockWebPage;
import com.chen.guo.crawler.util.WebAccessor;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QuarterlyMetricsTaskHist2 extends QuarterlyMetricsTask {
  private final static Pattern MATCHER = Pattern.compile("window.location='(.*?)'");

  private final int _startYear;

  /**
   * @param startYear  denotes the oldest year we care about. This startYear is inclusive
   * @param page       the StockWebPage
   * @param wantedRows provide a set of rows you want to fetch
   */
  public QuarterlyMetricsTaskHist2(int startYear, StockWebPage page, WebAccessor accessor, Set<String> wantedRows,
                                   String menuId, String menuName) {
    super(page, accessor, wantedRows, menuId, menuName);
    _startYear = startYear;
  }

  public QuarterlyMetricsTaskHist2(StockWebPage page, WebAccessor accessor, Set<String> wantedRows,
                                   String menuId, String menuName) {
    this(0, page, accessor, wantedRows, menuId, menuName);
  }

  @Override
  public TreeMap<Integer, Map<String, Double>> scrape(String menuPage) throws IOException {
    Document pageDoc = getWebAccessor().connect(menuPage);
    Element table = pageDoc.getElementById("content");
    validateHeader(menuPage, table);

    String yearlyUrl = getYearlyUrlFormat(pageDoc);
    Elements selections = pageDoc.getElementById("sel").children();
    List<String> items = selections.stream().map(Element::text).collect(Collectors.toList());
    if (!items.get(0).equals("最新")) {
      throw new RuntimeException(String.format("The style of the 年份 selection has changed for %s", _page));
    }
    List<String> wantedLinks = new ArrayList<>();
    for (int i = 1; i < items.size(); ++i) {
      Integer year = Integer.valueOf(items.get(i));
      if (year > _startYear) {
        wantedLinks.add(String.format("%s%s", yearlyUrl, year.toString()));
      }
    }


    HashMap<String, Elements> selectedRows = getSelectedRows(table);
    TreeMap<Integer, Map<String, Double>> results = new TreeMap<>();
    return results;
  }

  public String getYearlyUrlFormat(Document pageDoc) {
    Element javaScript = pageDoc.getElementById("content").getElementsByTag("script").first();
    String scriptContent = javaScript.childNode(0).toString();
    String location = extractWindowLocation(scriptContent);
    return StringUtil.resolve(pageDoc.baseUri(), location);
  }

  public static String extractWindowLocation(String scriptContent) {
    Matcher matcher = MATCHER.matcher(scriptContent);
    if (!matcher.find()) {
      throw new RuntimeException("Cannot find 'window.location' from the javascript" + scriptContent);
    }
    return matcher.group(1);
  }
}
