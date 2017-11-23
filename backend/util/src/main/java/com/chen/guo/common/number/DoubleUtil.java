package com.chen.guo.common.number;

import java.math.BigDecimal;
import java.math.MathContext;

public class DoubleUtil {
  /**
   * Convert string to double
   * Acceptable strings are:
   * -262.38
   * 0.25(元)
   * 0.25（元）
   * 0.25（万元）
   */
  public static Double parse(String str) {
    if (str == null || str.trim().isEmpty()) {
      return null;
    }

    try {
      return Double.valueOf(str);
    } catch (NumberFormatException e) {
      String[] split;
      if (str.contains("(")) {
        split = str.split("\\(");
      } else if (str.contains("（")) {
        split = str.split("（");
      } else {
        throw new RuntimeException("This value pattern is unknown " + str);
      }
      if (!split[1].contains("元")) {
        throw new RuntimeException("This value pattern is unknown " + str);
      }
      return Double.valueOf(split[0]);
    }
  }

  public static double roundToNSignificantNumbers(double d, int n) {
    BigDecimal bd = new BigDecimal(d);
    bd = bd.round(new MathContext(n));
    return bd.doubleValue();
  }
}
