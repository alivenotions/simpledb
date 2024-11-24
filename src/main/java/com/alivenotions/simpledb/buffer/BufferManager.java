package com.alivenotions.simpledb.buffer;

import com.alivenotions.simpledb.file.Block;
import com.alivenotions.simpledb.file.FileManager;
import com.alivenotions.simpledb.log.LogManager;

public class BufferManager {
  private final Buffer[] bufferPool;
  private int numAvailable;
  private static final long MAX_TIME_IN_MS = 10 * 1000;

  public BufferManager(FileManager fileManager, LogManager logManager, int numBuffs) {
    this.bufferPool = new Buffer[numBuffs];
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

  public synchronized void unpin(Buffer buffer) {
    buffer.unpin();
    if (!buffer.isPinned()) {
      numAvailable++;
      // How is this working?
      notifyAll();
    }
  }

  public synchronized Buffer pin(Block block) {
    try {
      long timestamp = System.currentTimeMillis();
      Buffer buffer = tryToPin(block);
      while (buffer == null && !waitingTooLong(timestamp)) {
        // how is this working?
        wait(MAX_TIME_IN_MS);
        buffer = tryToPin(block);
      }
      if (buffer == null) {
        throw new BufferAbortException();
      }
      return buffer;
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean waitingTooLong(long startTime) {
    return System.currentTimeMillis() - startTime > MAX_TIME_IN_MS;
  }

  private Buffer tryToPin(Block block) {
    Buffer buffer = findExistingBuffer(block);

    if (buffer == null) {
      buffer = chooseUnpinnedBuffer();
      if (buffer == null) {
        return null;
      }
      buffer.assignToBlock(block);
    }

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
    for (Buffer buffer : bufferPool) {
      if (!buffer.isPinned()) {
        return buffer;
      }
    }
    return null;
  }
}
