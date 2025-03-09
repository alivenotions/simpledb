package com.alivenotions.simpledb.log;

import static com.alivenotions.simpledb.log.LogRecordType.CHECKPOINT;

import com.alivenotions.simpledb.file.Page;
import com.alivenotions.simpledb.transaction.Transaction;

public final class CheckpointRecord implements LogRecord {
    public CheckpointRecord() {}

    @Override
    public int op() {
        return CHECKPOINT.code();
    }

    @Override
    public int txNum() {
        return -1;
    }

    @Override
    public void undo(Transaction tx) {}

    public String toString() {
        return "<CHECKPOINT>";
    }

    public static int writeToLog(LogManager logManager) {
        byte[] record = new byte[Integer.BYTES];
        Page p = Page.newPage(record);
        p.setInt(0, CHECKPOINT.code());
        return logManager.append(record);
    }
}
