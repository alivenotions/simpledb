package com.alivenotions.simpledb.log;

import static com.alivenotions.simpledb.log.LogRecordType.COMMIT;

import com.alivenotions.simpledb.file.Page;
import com.alivenotions.simpledb.transaction.Transaction;

public final class CommitRecord implements LogRecord {
    private final int txNum;

    public CommitRecord(Page page) {
        int txPosition = Integer.BYTES;
        this.txNum = page.getInt(txPosition);
    }

    public int op() {
        return COMMIT.code();
    }

    public int txNum() {
        return txNum;
    }

    public void undo(Transaction tx) {}

    public String toString() {
        return "<COMMIT %s>".formatted(txNum);
    }

    public static int writeToLog(LogManager logManager, int txNum) {
        byte[] record = new byte[Integer.BYTES * 2];
        Page p = Page.newPage(record);
        p.setInt(0, COMMIT.code());
        p.setInt(Integer.BYTES, txNum);
        return logManager.append(record);
    }
}
