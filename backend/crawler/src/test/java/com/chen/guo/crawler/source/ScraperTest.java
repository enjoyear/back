package com.chen.guo.crawler.source;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ScraperTest {

  @Test
  public void testCodeCriteria() {
    assertTrue(Scraper.checkForCodeCriteria("111222"));
    assertTrue(Scraper.checkForCodeCriteria("011222"));
    assertFalse(Scraper.checkForCodeCriteria("222"));
  }
}
