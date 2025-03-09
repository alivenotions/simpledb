package com.alivenotions.simpledb.log;

import static com.alivenotions.simpledb.log.LogRecordType.SETINT;

import com.alivenotions.simpledb.file.Block;
import com.alivenotions.simpledb.file.Page;
import com.alivenotions.simpledb.transaction.Transaction;

public final class SetIntRecord implements LogRecord {
    private final int txNum;
    private final int offset;
    private final int value;
    private final Block block;

    public SetIntRecord(Page page) {
        int txPosition = Integer.BYTES;
        this.txNum = page.getInt(txPosition);

        int fileNamePosition = txPosition + Integer.BYTES;
        String fileName = page.getString(fileNamePosition);

        int blockNumPosition = fileNamePosition + Page.maxLength(fileName.length());
        int blockNum = page.getInt(blockNumPosition);
        this.block = Block.of(fileName, blockNum);

        int offsetPosition = blockNumPosition + Integer.BYTES;
        this.offset = page.getInt(offsetPosition);

        int oldValuePosition = offsetPosition + Integer.BYTES;
        this.value = page.getInt(oldValuePosition);
    }

    @Override
    public int op() {
        return SETINT.code();
    }

    @Override
    public int txNum() {
        return txNum;
    }

    public String toString() {
        return "<SETINT %s %s %s %s>".formatted(txNum, block, offset, value);
    }

    @Override
    public void undo(Transaction tx) {
        tx.pin(block);
        tx.setInt(block, offset, value, false);
        tx.unpin(block);
    }

    public static int writeToLog(
            LogManager logManager, int txNum, Block block, int offset, int value) {
        int txPosition = Integer.BYTES;
        int fileNamePosition = txPosition + Integer.BYTES;
        int blockNumPosition = fileNamePosition + Page.maxLength(block.fileName().length());
        int offsetPosition = blockNumPosition + Integer.BYTES;
        int oldValuePosition = offsetPosition + Integer.BYTES;
        int recordSize = oldValuePosition + Integer.BYTES;

        byte[] record = new byte[recordSize];
        Page p = Page.newPage(record);
        p.setInt(0, SETINT.code());
        p.setInt(txPosition, txNum);
        p.setString(fileNamePosition, block.fileName());
        p.setInt(blockNumPosition, block.number());
        p.setInt(offsetPosition, offset);
        p.setInt(oldValuePosition, value);
        return logManager.append(record);
    }
}
