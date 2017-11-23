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
   * Convert a date string to an integer yyyyMMdd
   */
  public static int getDateInt(String dateString, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return getDateInt(LocalDate.parse(dateString, formatter));
  }

  public static int getDateInt(LocalDate date) {
    return date.getYear() * 10000 + date.getMonthValue() * 100 + date.getDayOfMonth();
  }
}
