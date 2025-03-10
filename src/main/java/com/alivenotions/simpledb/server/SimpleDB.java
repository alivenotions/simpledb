package com.alivenotions.simpledb.server;

import com.alivenotions.simpledb.buffer.BufferManager;
import com.alivenotions.simpledb.file.FileManager;
import com.alivenotions.simpledb.log.LogManager;
import java.io.File;

public class SimpleDB {
  public static final String LOG_FILE_NAME = "db.log";

  private final FileManager fileManager;
  private final LogManager logManager;
  private final BufferManager bufferManager;

  public SimpleDB(String dirName, int blockSizeInBytes, int bufferPoolSize) {
    this.fileManager = new FileManager(new File(dirName), blockSizeInBytes);
    this.logManager = new LogManager(fileManager, LOG_FILE_NAME);
    this.bufferManager = new BufferManager(fileManager, logManager, bufferPoolSize);
  }

  public FileManager fileManager() {
    return fileManager;
  }

  public LogManager logManager() {
    return logManager;
  }

  public BufferManager bufferManager() {
    return bufferManager;
  }
}
