package com.alivenotions.simpledb.log;

import com.alivenotions.simpledb.file.Page;
import com.alivenotions.simpledb.transaction.Transaction;

public sealed interface LogRecord
        permits CheckpointRecord,
                StartRecord,
                CommitRecord,
                RollbackRecord,
                SetIntRecord,
                SetStringRecord {

    int op();

    int txNum();

    void undo(Transaction tx);

    static LogRecord create(byte[] data) {
        Page page = Page.newPage(data);
        return switch (LogRecordType.fromCode(page.getInt(0))) {
            case CHECKPOINT -> new CheckpointRecord();
            case START -> new StartRecord(page);
            case COMMIT -> new CommitRecord(page);
            case ROLLBACK -> new RollbackRecord(page);
            case SETINT -> new SetIntRecord(page);
            case SETSTRING -> new SetStringRecord(page);
        };
    }
}
