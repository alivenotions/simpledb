package com.alivenotions.simpledb.utils;

public class CacheElement<K, V> {
  private K key;
  private V value;

  private CacheElement(K key, V value) {
    this.key = key;
    this.value = value;
  }

  public static <K, V> CacheElement<K, V> of(K key, V value) {
    return new CacheElement<K, V>(key, value);
  }

  public K key() {
    return key;
  }

  public void setKey(K key) {
    this.key = key;
  }

  public V value() {
    return value;
  }

  public void setValue(V value) {
    this.value = value;
  }
}
