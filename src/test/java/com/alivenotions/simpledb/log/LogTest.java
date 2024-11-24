package com.alivenotions.simpledb.log;

import static com.alivenotions.simpledb.server.SimpleDB.LOG_FILE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.alivenotions.simpledb.file.Page;
import com.alivenotions.simpledb.server.SimpleDB;
import java.io.File;
import java.util.Iterator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LogTest {
  private LogManager logManager;
  private SimpleDB db;

  @BeforeEach
  void setup() {
    db = new SimpleDB("testdb", 400, 8);
    logManager = db.logManager();
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
  void basic_test() {
    createRecords(1, 40);
    // The last seven records are not flushed yet.
    assertLogFileContains(33);

    createRecords(41, 70);
    // The last seven records are not flushed yet.
    assertLogFileContains(66);

    logManager.flush(68);
    // Since 69 and 70 were in the same page as 68, they are also flushed.
    assertLogFileContains(70);
  }

  @Test
  void writes_once_for_first_block_with_auto_flush() {
    createRecords(1, 11);

    var stats = db.fileManager().fileStatistics(LOG_FILE_NAME);
    assertEquals(1, stats.blocksWritten());
  }

  @Test
  void writes_twice_for_second_block_after_a_flush() {
    createRecords(1, 11);
    logManager.flush(11);

    var stats = db.fileManager().fileStatistics(LOG_FILE_NAME);
    assertEquals(2, stats.blocksWritten());
  }

  private void assertLogFileContains(int upper) {
    int bounds = upper;
    Iterator<byte[]> iterator = logManager.iterator();
    while (iterator.hasNext()) {
      byte[] record = iterator.next();
      Page page = Page.newPage(record);
      String string = page.getString(0);
      int numPosition = Page.maxLength(string.length());
      int value = page.getInt(numPosition);
      assertEquals("record" + bounds, string);
      assertEquals(bounds + 100, value);
      bounds--;
    }
  }

  private void createRecords(int start, int end) {
    for (int i = start; i <= end; i++) {
      byte[] record = createLogRecord("record" + i, i + 100);
      logManager.append(record);
    }
  }

  private byte[] createLogRecord(String s, int n) {
    int nPosition = Page.maxLength(s.length());
    byte[] bytes = new byte[nPosition + Integer.BYTES];
    Page page = Page.newPage(bytes);
    page.setString(0, s);
    page.setInt(nPosition, n);
    return bytes;
  }
}
