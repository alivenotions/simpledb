package com.alivenotions.simpledb.buffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.alivenotions.simpledb.file.Block;
import com.alivenotions.simpledb.file.Page;
import com.alivenotions.simpledb.server.SimpleDB;
import java.io.File;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BufferTest {
  private BufferManager bufferManager;
  private SimpleDB db;

  @BeforeEach
  void setup() {
    db = new SimpleDB("testdb", 400, 3);
    bufferManager = db.bufferManager();
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
  void evicting_modified_unpinned_buffer_from_pool_flushes_to_disk() {
    Buffer buffer1 = bufferManager.pinBufferToBlock(Block.of("testfile", 1));
    Page page1 = buffer1.page();
    int number = page1.getInt(80);
    page1.setInt(80, number + 1);
    buffer1.setModified(1, 0);
    bufferManager.unpinBuffer(buffer1);

    // Since there are only 3 buffers in the pool, one of these pins will flush buffer1 to disk
    bufferManager.pinBufferToBlock(Block.of("testfile", 2));
    bufferManager.pinBufferToBlock(Block.of("testfile", 3));
    bufferManager.pinBufferToBlock(Block.of("testfile", 4));

    Page firstUpdatePage = Page.newPage(400);
    db.fileManager().readBlockIntoPage(Block.of("testfile", 1), firstUpdatePage);
    final int updatedInt = firstUpdatePage.getInt(80);

    final long blocksWritten = db.fileManager().fileStatistics("testfile").blocksWritten();
    assertEquals(1, updatedInt);
    assertEquals(1, blocksWritten);
  }

  @Test
  void evicting_unmodified_unpinned_buffer_from_pool_does_not_flush_to_disk() {
    Buffer buffer1 = bufferManager.pinBufferToBlock(Block.of("testfile", 1));
    bufferManager.unpinBuffer(buffer1);

    bufferManager.pinBufferToBlock(Block.of("testfile", 2));
    bufferManager.pinBufferToBlock(Block.of("testfile", 3));
    bufferManager.pinBufferToBlock(Block.of("testfile", 4));

    Page firstUpdatePage = Page.newPage(400);
    db.fileManager().readBlockIntoPage(Block.of("testfile", 1), firstUpdatePage);
    final int updatedInt = firstUpdatePage.getInt(80);

    final long blocksWritten = db.fileManager().fileStatistics("testfile").blocksWritten();
    assertEquals(0, updatedInt);
    assertEquals(0, blocksWritten);
  }

  @Test
  void reuses_buffer_when_pinned_to_same_block() {
    Buffer buffer1 = bufferManager.pinBufferToBlock(Block.of("testfile", 1));
    Buffer buffer2 = bufferManager.pinBufferToBlock(Block.of("testfile", 1));
    Buffer buffer3 = bufferManager.pinBufferToBlock(Block.of("testfile", 2));

    assertEquals(buffer1, buffer2);
    assertEquals(1, bufferManager.available());
  }
}
