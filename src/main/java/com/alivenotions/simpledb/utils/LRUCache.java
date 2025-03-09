package com.alivenotions.simpledb.utils;

import java.util.HashMap;
import java.util.Map;

public class LRUCache<K, V> implements Cache<K, V> {
  private int size;
  private Map<K, Node<CacheElement<K, V>>> linkedListNodeMap;
  private DoublyLinkedList<CacheElement<K, V>> doublyLinkedList;

  public LRUCache(int size) {
    this.size = size;
    this.linkedListNodeMap = new HashMap<>(size);
    this.doublyLinkedList = new DoublyLinkedList<>();
  }

  @Override
  public boolean put(K key, V value) {
    final CacheElement<K, V> cacheElement = CacheElement.of(key, value);
    Node<CacheElement<K, V>> newNode;

    if (linkedListNodeMap.containsKey(key)) {
      Node<CacheElement<K, V>> node = linkedListNodeMap.get(key);
    }

    return true;
  }

  @Override
  public V get(K key) {
    return null;
  }

  @Override
  public int size() {
    return size;
  }
}
