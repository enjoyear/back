package com.chen.guo.crawler.source;

import com.chen.guo.crawler.source.cfi.CfiScraper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class CfiScraperTest {

  @Test
  public void testCodeCriteria() {
    assertTrue(CfiScraper.checkForCodeCriteria("111222"));
    assertTrue(CfiScraper.checkForCodeCriteria("011222"));
    assertFalse(CfiScraper.checkForCodeCriteria("222"));
  }
}
