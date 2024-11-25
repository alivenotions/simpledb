package com.alivenotions.simpledb.buffer;

import com.alivenotions.simpledb.file.Block;
import com.alivenotions.simpledb.file.FileManager;
import com.alivenotions.simpledb.log.LogManager;
import com.alivenotions.simpledb.metadata.BufferStatistics;
import java.time.Clock;

public class BufferManager {
  private final Buffer[] bufferPool;
  private int numAvailable;
  private static final long MAX_TIME_IN_MS = 10 * 1000;
  private final BufferStatistics stats;

  public BufferManager(FileManager fileManager, LogManager logManager, int numBuffs) {
    this.bufferPool = new Buffer[numBuffs];
    this.stats = new BufferStatistics();
    numAvailable = numBuffs;
    for (int i = 0; i < numBuffs; i++) {
      bufferPool[i] = new Buffer(fileManager, logManager);
    }
  }

  public synchronized int available() {
    return numAvailable;
  }

  public synchronized void flushAll(int txNum) {
    for (Buffer buffer : bufferPool) {
      if (buffer.modifyingTx() == txNum) {
        buffer.flush();
      }
    }
  }

  public synchronized void unpinBuffer(Buffer buffer) {
    buffer.unpin();
    if (!buffer.isPinned()) {
      numAvailable++;
      notifyAll();
    }
  }

  public synchronized Buffer pinBufferToBlock(Block block) {
    try {
      long timestamp = System.currentTimeMillis();
      Buffer buffer = tryToPin(block);
      while (buffer == null && !waitingTooLong(timestamp)) {
        wait(MAX_TIME_IN_MS);
        buffer = tryToPin(block);
      }
      // If the buffer is still null after waiting for the max time,
      // we assume there's a deadlock and throw an exception for the client
      // to handle (mostly to try the request again and request buffers again).
      if (buffer == null) {
        throw new BufferAbortException();
      }
      return buffer;
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean waitingTooLong(long startTime) {
    return Clock.systemUTC().millis() - startTime > MAX_TIME_IN_MS;
  }

  private Buffer tryToPin(Block block) {
    Buffer buffer = findExistingBuffer(block);

    if (buffer == null) {
      stats.incrementBuffersMissed();
      buffer = chooseUnpinnedBuffer();
      if (buffer == null) {
        return null;
      }
      buffer.assignToBlock(block);
    }

    stats.incrementBuffersHit();
    if (!buffer.isPinned()) {
      numAvailable--;
    }
    buffer.pin();
    return buffer;
  }

  private Buffer findExistingBuffer(Block block) {
    for (Buffer buffer : bufferPool) {
      Block possibleBufferBlock = buffer.block();
      if (possibleBufferBlock != null && possibleBufferBlock.equals(block)) {
        return buffer;
      }
    }
    return null;
  }

  private Buffer chooseUnpinnedBuffer() {
    // This is a naive implementation of buffer selection. Picks the first
    // unpinned buffer it finds. A more sophisticated approach would be to
    // choose the least recently used buffer. Or a clock algorithm.
    for (Buffer buffer : bufferPool) {
      if (!buffer.isPinned()) {
        return buffer;
      }
    }
    return null;
  }
}
