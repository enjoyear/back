package com.chen.guo.crawler.source.cfi.task;

import org.testng.Assert;
import org.testng.annotations.Test;

public class QuarterlyMetricsTaskHist2Test {

  @Test
  public void testJavascriptLocationExtract() {
    String js = "function fun(){ var sel=document.getElementById('sel'); window.location='/quote.aspx?contenttype=lrfpb_x&stockid=11576&jzrq='+sel.options[sel.selectedIndex].value;}";
    String extracted = QuarterlyMetricsTaskHistType2.extractWindowLocation(js);
    Assert.assertEquals(extracted, "/quote.aspx?contenttype=lrfpb_x&stockid=11576&jzrq=");
  }
}
