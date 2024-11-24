package com.alivenotions.simpledb.log;

import com.alivenotions.simpledb.file.Block;
import com.alivenotions.simpledb.file.FileManager;
import com.alivenotions.simpledb.file.Page;
import java.util.Iterator;

public class LogIterator implements Iterator<byte[]> {
  private final FileManager fileManager;
  private Block block;
  private final Page page;
  private int currentPosition;

  public LogIterator(FileManager fileManager, Block block) {
    this.fileManager = fileManager;
    this.block = block;
    byte[] bytes = new byte[fileManager.blockSize()];
    page = Page.newPage(bytes);
    moveToBlock(block);
  }

  public boolean hasNext() {
    return currentPosition < fileManager.blockSize() || block.number() > 0;
  }

  public byte[] next() {
    if (currentPosition == fileManager.blockSize()) {
      block = Block.of(block.fileName(), block.number() - 1);
      moveToBlock(block);
    }

    byte[] record = page.getBytes(currentPosition);
    currentPosition += Integer.BYTES + record.length;
    return record;
  }

  private void moveToBlock(Block block) {
    fileManager.readBlockIntoPage(block, page);

    currentPosition = page.getInt(0);
  }
}
