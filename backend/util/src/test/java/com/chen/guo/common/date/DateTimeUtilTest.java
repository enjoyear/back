package com.chen.guo.common.date;

import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.testng.Assert.assertEquals;

public class DateTimeUtilTest {
  @Test
  public void testGetDate() {
    LocalDate date = DateTimeUtil.getDate("2017-01-31", "yyyy-MM-dd");
    assertEquals(date.getYear(), 2017);
    assertEquals(date.getMonth(), Month.JANUARY);
    assertEquals(date.getDayOfMonth(), 31);
  }

  @Test
  public void testGetYearMonthInt() {
    assertEquals(DateTimeUtil.getDateInt("2017-01-31", "yyyy-MM-dd"), 201701);
  }
}
