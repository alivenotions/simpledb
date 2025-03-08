package com.alivenotions.simpledb.buffer;

import com.alivenotions.simpledb.file.Block;
import com.alivenotions.simpledb.file.FileManager;
import com.alivenotions.simpledb.file.Page;
import com.alivenotions.simpledb.log.LogManager;

public class Buffer {
  private final FileManager fileManager;
  private final LogManager logManager;
  private final Page page;
  private Block block = null;
  private int pins = 0;
  private int txNum = -1;
  private int lsn = -1;

  public Buffer(FileManager fileManager, LogManager logManager) {
    this.fileManager = fileManager;
    this.logManager = logManager;
    page = Page.newPage(fileManager.blockSize());
  }

  public Page page() {
    return page;
  }

  public Block block() {
    return block;
  }

  public void setModified(int txNum, int lsn) {
    this.txNum = txNum;
    if (lsn >= 0) this.lsn = lsn;
  }

  public boolean isPinned() {
    return pins > 0;
  }

  public int modifyingTx() {
    return txNum;
  }

  void assignToBlock(Block block) {
    flush();
    this.block = block;
    fileManager.readBlockIntoPage(this.block, page);
    pins = 0;
  }

  void flush() {
    if (txNum >= 0) {
      logManager.flush(lsn);
      fileManager.writePageToBlock(block, page);
      txNum = -1;
    }
  }

  void pin() {
    pins++;
  }

  void unpin() {
    pins--;
  }
}
