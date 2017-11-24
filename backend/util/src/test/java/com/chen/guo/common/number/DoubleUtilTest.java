package com.chen.guo.common.number;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class DoubleUtilTest {
  @Test
  public void testParse() {
    assertEquals(DoubleUtil.parse("0.39元"), 0.39);
    assertEquals(DoubleUtil.parse("0.39(元)"), 0.39);
    assertEquals(DoubleUtil.parse("1000.1亿"), 1.0001E11);
    assertEquals(DoubleUtil.parse("0.39(亿)"), 3.9E7);
    assertEquals(DoubleUtil.parse("0.25（元）"), 0.25);
    assertEquals(DoubleUtil.parse("-0.25（万元）"), -2500.0);
    assertEquals(DoubleUtil.parse("-2534.65"), -2534.65);
  }

  @Test
  public void testRoundToNSignificantNumbers() {
    assertEquals(DoubleUtil.roundToNSignificantNumbers(1.123456, 1), 1.0);
    assertEquals(DoubleUtil.roundToNSignificantNumbers(1.123456, 2), 1.1);
    assertEquals(DoubleUtil.roundToNSignificantNumbers(1.123456, 3), 1.12);

    assertEquals(DoubleUtil.roundToNSignificantNumbers(1.98765, 1), 2.0);
    assertEquals(DoubleUtil.roundToNSignificantNumbers(1.98765, 2), 2.0);
    assertEquals(DoubleUtil.roundToNSignificantNumbers(1.98765, 3), 1.99);

    assertEquals(DoubleUtil.roundToNSignificantNumbers(19.8765, 1), 20.0);
    assertEquals(DoubleUtil.roundToNSignificantNumbers(19.8765, 2), 20.0);
    assertEquals(DoubleUtil.roundToNSignificantNumbers(19.8765, 3), 19.9);
  }
}
