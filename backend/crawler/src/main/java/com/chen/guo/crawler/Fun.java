package com.chen.guo.crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Fun {
  public static void main(String[] args) {
    List<String> headers = new ArrayList<>();
    headers.add("Important");
    headers.add("Obvious");
    headers.add("Nice to have");
    headers.add("Necessary");
    headers.add("Critical");
    headers.add("Heavy");
    headers.add("Optional");
    headers.add("Almost Useless");
    System.out.println(Arrays.toString(headers.toArray()));

    List<String> content = new ArrayList<>();
    content.add("Important");
    content.add("Obvious");
    content.add(UUID.randomUUID().toString());
    content.add("Necessary");
    content.add(String.valueOf(System.currentTimeMillis()));
    content.add("Heavy");
    content.add("Optional");
    content.add("Almost Useless");
    System.out.println(Arrays.toString(content.toArray()));
  }
}
