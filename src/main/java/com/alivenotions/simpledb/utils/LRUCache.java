package com.alivenotions.simpledb.utils;

import java.util.LinkedList;
import java.util.Map;

public class LRUCache<K, V> {
  private int size;
  private Map<K, LinkedList<CacheElement<K, V>>> linkedListNodeMap;
  private DoublyLinkedList<CacheElement<K, V>> doublyLinkedList;
}
