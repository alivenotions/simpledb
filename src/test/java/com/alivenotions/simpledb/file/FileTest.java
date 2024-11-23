package com.alivenotions.simpledb.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.alivenotions.simpledb.server.SimpleDB;
import org.junit.jupiter.api.Test;

public class FileTest {
  @Test
  public void writing_to_the_db_works() {

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

    assertEquals(344, page2.getInt(pos2));
    assertEquals("abcdefghijklm", page2.getString(pos1));
  }

  @Test
  public void file_manager_tracks_stats() {
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

    Page page3 = Page.newPage(fileManager.blockSize());
    fileManager.read(block, page3);

    FileStatistics stats = fileManager.fileStatistics("testfile");
    assertEquals(1, stats.blocksWritten());
    assertEquals(2, stats.blocksRead());
  }

  @Test
  public void file_manager_throws_when_stats_file_does_not_exist() {

    SimpleDB db = new SimpleDB("testdb", 400, 8);
    FileManager fileManager = db.fileManager();

    assertThrows(IllegalArgumentException.class, () -> fileManager.fileStatistics("testfile"));
  }
}
