package com.alivenotions.simpledb.metadata;

public class FileStatistics {
  private final String fileName;
  private long blocksWritten;
  private long blocksRead;

  public FileStatistics(String fileName) {
    this.fileName = fileName;
    this.blocksWritten = 0;
    this.blocksRead = 0;
  }

  public String fileName() {
    return fileName;
  }

  public long blocksWritten() {
    return blocksWritten;
  }

  public long blocksRead() {
    return blocksRead;
  }

  public void incrementBlocksWritten() {
    blocksWritten++;
  }

  public void incrementBlocksRead() {
    blocksRead++;
  }
}
