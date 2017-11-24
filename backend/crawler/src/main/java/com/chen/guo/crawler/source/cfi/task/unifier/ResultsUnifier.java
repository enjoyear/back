package com.chen.guo.crawler.source.cfi.task.unifier;

import com.chen.guo.accounting.BalanceSheetConstants;
import com.chen.guo.accounting.IncomeStatementConstants;
import com.chen.guo.common.number.DoubleUtil;

import java.util.*;

public class ResultsUnifier {
  private static final int DEFAULT_NUMBER_OF_SIGNIFICANT_FIGURES = 5;
  private static final MetricUnifier NET_INCOME_UNIFIER = new MetricUnifier(
      "归属母公司净利润（元）", IncomeStatementConstants.NI_ATTRIBUTABLE_TO_SHAREHOLDERS,
      val -> {
        val = val / 1E8; //yuan -> yi yuan
        return DoubleUtil.roundToNSignificantNumbers(val, DEFAULT_NUMBER_OF_SIGNIFICANT_FIGURES);
      });

  private static final MetricUnifier NET_REVENUE_UNIFIER = new MetricUnifier(
      "一、营业总收入", IncomeStatementConstants.NET_REVENUE,
      val -> {
        val = val / 1E4; //wan yuan -> yi yuan
        return DoubleUtil.roundToNSignificantNumbers(val, DEFAULT_NUMBER_OF_SIGNIFICANT_FIGURES);
      });

  private static final MetricUnifier TOTAL_CAPITAL_UNIFIER = new MetricUnifier(
      "1.A股(股)", BalanceSheetConstants.TOTAL_SHARE_CAPITAL,
      val -> DoubleUtil.roundToNSignificantNumbers(val, DEFAULT_NUMBER_OF_SIGNIFICANT_FIGURES));

  private static final Map<String, MetricUnifier> unifierMap = new HashMap<>();

  {
    List<MetricUnifier> unifiers = new ArrayList<>();
    unifiers.add(NET_INCOME_UNIFIER);
    unifiers.add(NET_REVENUE_UNIFIER);
    unifiers.add(TOTAL_CAPITAL_UNIFIER);

    for (MetricUnifier unifier : unifiers) {
      unifierMap.put(unifier.getRowName(), unifier);
    }
  }

  public TreeMap<Integer, Map<String, Double>> unify(TreeMap<Integer, Map<String, Double>> map) {
    TreeMap<Integer, Map<String, Double>> unified = new TreeMap<>();
    for (Map.Entry<Integer, Map<String, Double>> item : map.entrySet()) {
      Map<String, Double> unifiedMap = new HashMap<>();
      for (Map.Entry<String, Double> toUnify : item.getValue().entrySet()) {
        MetricUnifier metricUnifier = unifierMap.get(toUnify.getKey());
        if (metricUnifier != null) {
          unifiedMap.put(metricUnifier.getUniformName(), metricUnifier.getValueUnifier().apply(toUnify.getValue()));
        }
      }
      unified.put(item.getKey(), unifiedMap);
    }
    return unified;
  }
}
