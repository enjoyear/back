package com.chen.guo.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
  public static LocalDate getDate(String dateString, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return LocalDate.parse(dateString, formatter);
  }

  public static int getYearMonthInt(String dateString, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    LocalDate date = LocalDate.parse(dateString, formatter);
    return date.getYear() * 100 + date.getMonthValue();
  }
}
