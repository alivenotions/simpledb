package com.alivenotions.simpledb.log;

// Log Sequence Number. Trying to see how good this is to wrap it in a class but
// let's see it, and we will optimize it later.
public final class LSN {
  private int current;
  private int lastSaved;

  private LSN() {
    this.current = 0;
    this.lastSaved = 0;
  }

  public static LSN newSequence() {
    return new LSN();
  }

  public int current() {
    return current;
  }

  public int lastSaved() {
    return lastSaved;
  }

  public void checkpoint() {
    lastSaved = current;
  }

  public void increment() {
    current++;
  }
}
