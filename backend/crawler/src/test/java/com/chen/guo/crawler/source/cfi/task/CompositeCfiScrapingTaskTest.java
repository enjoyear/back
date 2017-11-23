package com.chen.guo.crawler.source.cfi.task;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class CompositeCfiScrapingTaskTest {
  @Test
  public void testMapMerge() {
    Map<Integer, Map<String, Double>> result = new HashMap<>();
    Map<String, Double> metric1 = new HashMap<>();
    metric1.put("a", 10.0);
    metric1.put("b", 20.0);
    result.put(201701, metric1);

    Map<Integer, Map<String, Double>> increment = new HashMap<>();
    Map<String, Double> metric2 = new HashMap<>();
    metric2.put("a", 11.0);
    increment.put(201701, metric2);

    Map<String, Double> metric3 = new HashMap<>();
    metric3.put("c", 30.0);
    increment.put(201704, metric3);

    CompositeCfiScrapingTask.merge(result, increment);

    Assert.assertEquals(result.size(), 2);
    Map<String, Double> merged201701 = result.get(201701);
    Assert.assertEquals(merged201701.size(), 2);
    Assert.assertEquals(merged201701.get("a"), 11.0);
    Assert.assertEquals(merged201701.get("b"), 20.0);

    Map<String, Double> merged201704 = result.get(201704);
    Assert.assertEquals(merged201704.size(), 1);
    Assert.assertEquals(merged201704.get("c"), 30.0);
  }
}
