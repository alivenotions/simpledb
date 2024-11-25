package com.alivenotions.simpledb.metadata;

public class BufferStatistics {
  private long buffersHit;
  private long buffersMissed;

  public BufferStatistics() {
    this.buffersHit = 0;
    this.buffersMissed = 0;
  }

  public long buffersHit() {
    return buffersHit;
  }

  public long buffersMissed() {
    return buffersMissed;
  }

  public void incrementBuffersHit() {
    buffersHit++;
  }

  public void incrementBuffersMissed() {
    buffersMissed++;
  }
}
