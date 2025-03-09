package com.alivenotions.simpledb.utils;

public interface Cache<K, V> {
  boolean put(K key, V value);

  V get(K key);

  int size();
}
