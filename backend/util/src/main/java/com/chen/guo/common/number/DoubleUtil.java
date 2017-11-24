package com.chen.guo.common.number;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoubleUtil {
  private static final Pattern PATTERN = Pattern.compile("(.*?)[（\\(]*(\\p{sc=Han}*)[）\\)]*");

  /**
   * Convert string to double
   * Acceptable strings are:
   * -262.38
   * 0.25(元)
   * 0.25（亿）
   * 0.25（万元）
   */
  public static Double parse(String str) {
    if (str == null || str.trim().isEmpty()) {
      return null;
    }

    Matcher matcher = PATTERN.matcher(str);
    if (!matcher.matches()) {
      throw new RuntimeException(String.format("Unable to match %s with pattern %s", str, PATTERN));
    }
    String number = matcher.group(1);
    String unit = matcher.group(2);

    double multiplier = 1;
    if (!unit.trim().isEmpty()) {
      switch (unit) {
        case "元":
          multiplier = 1;
          break;
        case "万":
        case "万元":
          multiplier = 1E4;
          break;
        case "亿":
        case "亿元":
          multiplier = 1E8;
          break;
        default:
          throw new RuntimeException(String.format("Unsupported unit '%s' in '%s'", unit, str));
      }
    }
    return Double.valueOf(number) * multiplier;
  }

  public static double roundToNSignificantNumbers(double d, int n) {
    BigDecimal bd = new BigDecimal(d);
    bd = bd.round(new MathContext(n));
    return bd.doubleValue();
  }
}
