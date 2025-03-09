package com.alivenotions.simpledb.transaction;

import com.alivenotions.simpledb.buffer.BufferManager;
import com.alivenotions.simpledb.file.Block;
import com.alivenotions.simpledb.file.FileManager;
import com.alivenotions.simpledb.log.LogManager;

public class Transaction {
    private static int nextTxNum = 0;
    private static final int END_OF_FILE = -1;
    private FileManager fileManager;
    private BufferManager bufferManager;
    private int txNum;

    public Transaction(FileManager fm, LogManager lm, BufferManager bm) {
        this.fileManager = fm;
        this.bufferManager = bm;
        txNum = nextTxNumber();
    }

    public void commit() {}

    public void rollback() {}

    public void recover() {}

    public void pin(Block block) {}

    public void unpin(Block block) {}

    public int getInt(Block block, int offset) {
        return -1;
    }

    public void setInt(Block block, int offset, int val, boolean okToLog) {}

    public String getString(Block block, int offset) {
        return null;
    }

    public void setString(Block block, int offset, String val, boolean okToLog) {}

    public int availableBuffers() {
        return 0;
    }

    public int size(String filename) {
        return -1;
    }

    public Block append(String filename) {
        return null;
    }

    public int blockSize() {
        return -1;
    }

    public static synchronized int nextTxNumber() {
        nextTxNum++;
        return nextTxNum;
    }
}
