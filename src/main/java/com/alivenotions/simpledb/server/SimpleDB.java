package com.alivenotions.simpledb.server;

import com.alivenotions.simpledb.file.FileManager;
import java.io.File;

public class SimpleDB {
  private final FileManager fileManager;

  public SimpleDB(String databaseName, int blockSizeInBytes, int bufferPoolSize) {
    this.fileManager = new FileManager(new File(databaseName), blockSizeInBytes);
  }

  public FileManager fileManager() {
    return fileManager;
  }
}
