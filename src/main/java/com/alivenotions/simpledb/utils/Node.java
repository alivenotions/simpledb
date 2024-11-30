package com.alivenotions.simpledb.utils;

public class Node<T> {
  private final T value;
  private final DoublyLinkedList<T> list;
  private Node<T> next;
  private Node<T> prev;

  public Node(T value, Node<T> next, DoublyLinkedList<T> list) {
    this.value = value;
    this.next = next;
    this.setPrev(next.prev());
    this.prev.setNext(this);
    this.next.setPrev(this);
    this.list = list;
  }

  public T getElement() {
    return value;
  }

  public void detach() {
    this.prev.setNext(this.next());
    this.next.setPrev(this.prev());
  }

  public DoublyLinkedList<T> getListReference() {
    return this.list;
  }

  public Node<T> setPrev(Node<T> prev) {
    this.prev = prev;
    return this;
  }

  public Node<T> setNext(Node<T> next) {
    this.next = next;
    return this;
  }

  public Node<T> prev() {
    return this.prev;
  }

  public Node<T> next() {
    return this.next;
  }

  public Node<T> search(T value) {
    return this.getElement() == value ? this : this.next().search(value);
  }
}
