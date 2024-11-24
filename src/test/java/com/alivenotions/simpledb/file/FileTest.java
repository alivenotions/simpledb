package com.alivenotions.simpledb.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.alivenotions.simpledb.server.SimpleDB;
import java.io.File;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FileTest {
  private FileManager fileManager;
  private static final String TEST_FILE_NAME = "testfile";

  @BeforeEach
  void setup() {
    SimpleDB db = new SimpleDB("testdb", 400, 8);
    fileManager = db.fileManager();
  }

  @AfterEach
  void tearDown() {
    File dir = new File("testdb");
    File[] files = dir.listFiles();
    assert files != null;
    for (File file : files) {
      file.delete();
    }
  }

  @Test
  public void writing_to_the_db_works() {
    Block block = new Block(TEST_FILE_NAME, 2);

    Page page1 = Page.newPage(fileManager.blockSize());
    int pos1 = 88;
    page1.setString(pos1, "abcdefghijklm");
    int size = Page.maxLength("abcdefghijklm".length());
    int pos2 = pos1 + size;
    page1.setInt(pos2, 344);
    fileManager.writePageToBlock(block, page1);

    Page page2 = Page.newPage(fileManager.blockSize());
    fileManager.readBlockIntoPage(block, page2);

    assertEquals(344, page2.getInt(pos2));
    assertEquals("abcdefghijklm", page2.getString(pos1));
  }

  @Test
  public void file_manager_tracks_stats() {
    Block block = new Block(TEST_FILE_NAME, 2);

    Page page1 = Page.newPage(fileManager.blockSize());
    int pos1 = 88;
    page1.setString(pos1, "abcdefghijklm");
    int size = Page.maxLength("abcdefghijklm".length());
    int pos2 = pos1 + size;
    page1.setInt(pos2, 344);
    fileManager.writePageToBlock(block, page1);

    Page page2 = Page.newPage(fileManager.blockSize());
    fileManager.readBlockIntoPage(block, page2);

    Page page3 = Page.newPage(fileManager.blockSize());
    fileManager.readBlockIntoPage(block, page3);

    FileStatistics stats = fileManager.fileStatistics(TEST_FILE_NAME);
    assertEquals(1, stats.blocksWritten());
    assertEquals(2, stats.blocksRead());
  }

  @Test
  public void file_manager_throws_when_stats_file_does_not_exist() {
    assertThrows(IllegalArgumentException.class, () -> fileManager.fileStatistics(TEST_FILE_NAME));
  }
}
