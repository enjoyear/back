package com.chen.guo.crawler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Fun2 {
  public static void main(String[] args) {
    ExecutorService es = Executors.newFixedThreadPool(2);

    Runnable r1 = new Runnable() {
      @Override
      public void run() {
        throw new RuntimeException("aa)");
      }
    };

    Runnable r2 = new MyRunnable("r2");
    Runnable r3 = new MyRunnable("r3");
    Runnable r4 = new MyRunnable("r4");
    Runnable r5 = new MyRunnable("r5");

    es.submit(r1);
    es.submit(r2);
    es.submit(r3);
    es.submit(r4);
    es.submit(r5);

    es.shutdown();

  }

  public static class MyRunnable implements Runnable {
    private String _id;

    public MyRunnable(String id) {
      _id = id;
    }

    @Override
    public void run() {
      System.out.printf("%s at %d%n", _id, Thread.currentThread().getId());
    }
  }
}
