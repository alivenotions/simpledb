package com.alivenotions.simpledb.log;

import com.alivenotions.simpledb.file.Block;
import com.alivenotions.simpledb.file.FileManager;
import com.alivenotions.simpledb.file.Page;
import java.util.Iterator;

public class LogManager {
  private final FileManager fileManager;
  private final String logFile;
  private final Page logPage;
  private Block currentBlock;
  private final LSN lsn = LSN.newSequence();

  public LogManager(FileManager fileManager, String logFile) {
    this.fileManager = fileManager;
    this.logFile = logFile;
    byte[] bytes = new byte[fileManager.blockSize()];
    logPage = Page.newPage(bytes);

    int logSize = fileManager.length(logFile);
    if (logSize == 0) {
      currentBlock = appendNewBlock();
    } else {
      currentBlock = Block.of(logFile, logSize - 1);
      fileManager.readBlockIntoPage(currentBlock, logPage);
    }
  }

  public void flush(int lsn) {
    if (lsn >= this.lsn.lastSaved()) {
      flush();
    }
  }

  public Iterator<byte[]> iterator() {
    // Add this back if this is needed somewhere
    //    flush();
    return new LogIterator(fileManager, currentBlock);
  }

  public synchronized int append(byte[] logRecord) {
    int boundary = logPage.getInt(0);
    int recordSize = logRecord.length;
    int bytesNeeded = recordSize + Integer.BYTES;
    if (boundary - bytesNeeded < Integer.BYTES) {
      flush();
      currentBlock = appendNewBlock();
      boundary = logPage.getInt(0);
    }
    int recordPosition = boundary - bytesNeeded;
    logPage.setBytes(recordPosition, logRecord);
    logPage.setInt(0, recordPosition);

    lsn.increment();
    return lsn.current();
  }

  private Block appendNewBlock() {
    Block block = fileManager.appendEmptyBlock(logFile);
    logPage.setInt(0, fileManager.blockSize());
    fileManager.writePageToBlock(block, logPage);
    return block;
  }

  private void flush() {
    fileManager.writePageToBlock(currentBlock, logPage);
    lsn.checkpoint();
  }
}
