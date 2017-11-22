package com.chen.guo.common.number;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class DoubleUtilTest {
  @Test
  public void testParse() {
    assertEquals(DoubleUtil.parse("-2534.65"), -2534.65);
    assertEquals(DoubleUtil.parse("0.39(元)"), 0.39);
    assertEquals(DoubleUtil.parse("0.25（元）"), 0.25);
    assertEquals(DoubleUtil.parse("0.25（万元）"), 0.25);
  }
}
