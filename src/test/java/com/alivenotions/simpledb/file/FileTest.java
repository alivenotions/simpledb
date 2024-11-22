package com.alivenotions.simpledb.file;

import com.alivenotions.simpledb.server.SimpleDB;

public class FileTest {
  public static void main(String[] args) {
    SimpleDB db = new SimpleDB("testdb", 400, 8);
    FileManager fileManager = db.fileManager();

    Block block = new Block("testfile", 2);

    Page page1 = Page.newPage(fileManager.blockSize());
    int pos1 = 88;
    page1.setString(pos1, "abcdefghijklm");
    int size = Page.maxLength("abcdefghijklm".length());
    int pos2 = pos1 + size;
    page1.setInt(pos2, 344);
    fileManager.write(block, page1);

    Page page2 = Page.newPage(fileManager.blockSize());
    fileManager.read(block, page2);
  }
}
