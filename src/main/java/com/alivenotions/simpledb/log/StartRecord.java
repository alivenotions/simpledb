package com.alivenotions.simpledb.log;

import static com.alivenotions.simpledb.log.LogRecordType.START;

import com.alivenotions.simpledb.file.Page;
import com.alivenotions.simpledb.transaction.Transaction;

public final class StartRecord implements LogRecord {
    private final int txNum;

    public StartRecord(Page page) {
        int txPosition = Integer.BYTES;
        this.txNum = page.getInt(txPosition);
    }

    @Override
    public int op() {
        return START.code();
    }

    @Override
    public int txNum() {
        return txNum;
    }

    public String toString() {
        return "<START %s>".formatted(txNum);
    }

    @Override
    public void undo(Transaction tx) {}

    public static int writeToLog(LogManager logManager, int txNum) {
        byte[] record = new byte[Integer.BYTES * 2];
        Page p = Page.newPage(record);
        p.setInt(0, START.code());
        p.setInt(Integer.BYTES, txNum);
        return logManager.append(record);
    }
}
