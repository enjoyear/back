package com.chen.guo.common.date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
  /**
   * Parse the date string given the date pattern
   */
  public static LocalDate getDate(String dateString, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return LocalDate.parse(dateString, formatter);
  }

  /**
   * Convert a date string to an integer yyyyMM
   */
  public static int getYearMonthInt(String dateString, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    LocalDate date = LocalDate.parse(dateString, formatter);
    return date.getYear() * 100 + date.getMonthValue();
  }
}
