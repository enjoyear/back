package com.chen.guo.common.number;

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
}
