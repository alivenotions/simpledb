package com.alivenotions.simpledb.transaction;

import com.alivenotions.simpledb.buffer.Buffer;
import com.alivenotions.simpledb.buffer.BufferManager;
import java.util.logging.LogManager;

public class RecoveryManager {
    private Transaction tx;
    private int txNum;
    private LogManager logManager;
    private BufferManager bufferManager;

    public RecoveryManager(
            Transaction tx, int txNum, LogManager logManager, BufferManager bufferManager) {
        this.tx = tx;
        this.txNum = txNum;
        this.logManager = logManager;
        this.bufferManager = bufferManager;
    }

    public void commit() {}

    public void rollback() {}

    public void recover() {}

    public int setInt(Buffer buffer, int offset, int val) {
        return -1;
    }

    public int setString(Buffer buffer, int offset, String val) {
        return -1;
    }
}
