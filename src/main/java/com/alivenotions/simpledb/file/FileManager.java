package com.alivenotions.simpledb.file;

import static java.nio.file.Files.size;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FileManager {
  private final File databaseDirectory;
  private final int blockSize;
  private final boolean isNew;
  private final Map<String, RandomAccessFile> openFiles = new HashMap<>();

  public FileManager(File databaseDirectory, int blockSize) {
    this.databaseDirectory = databaseDirectory;
    this.blockSize = blockSize;

    isNew = !databaseDirectory.exists();

    if (isNew) {
      // TODO: Find what to do if mkdirs fail
      databaseDirectory.mkdirs();
    }

    for (String filename : Objects.requireNonNull(databaseDirectory.list())) {
      if (filename.startsWith("temp")) {
        // TODO: Find what to do if deletion fails
        new File(databaseDirectory, filename).delete();
      }
    }
  }

  public synchronized void read(Block block, Page page) {
    try {
      RandomAccessFile file = getFile(block.fileName());
      file.seek((long) block.number() * blockSize);
      file.getChannel().read(page.contents());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public synchronized void write(Block block, Page page) {
    try {
      RandomAccessFile file = getFile(block.fileName());
      file.seek((long) block.number() * blockSize);
      // Should we return the number of bytes written?
      file.getChannel().write(page.contents());
    } catch (IOException e) {
      throw new RuntimeException("cannot write to block" + block);
    }
  }

  public synchronized Block append(String filename) throws IOException {
    int newBlock = Math.toIntExact(size(Path.of(filename)));
    Block block = Block.of(filename, newBlock);
    byte[] bytes = new byte[blockSize];
    try {
      RandomAccessFile file = getFile(block.fileName());
      file.seek((long) block.number() * blockSize);
      file.write(bytes);
    } catch (IOException e) {
      throw new RuntimeException("cannot append in block" + block);
    }
    return block;
  }

  public int length(String filename) {
    try {
      RandomAccessFile file = getFile(filename);
      return (int) (file.length() / blockSize);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isNew() {
    return isNew;
  }

  public int blockSize() {
    return blockSize;
  }

  private RandomAccessFile getFile(String filename) throws FileNotFoundException {
    RandomAccessFile file = openFiles.get(filename);
    if (file == null) {
      File table = new File(databaseDirectory, filename);
      file = new RandomAccessFile(table, "rws");
      openFiles.put(filename, file);
    }
    return file;
  }
}
