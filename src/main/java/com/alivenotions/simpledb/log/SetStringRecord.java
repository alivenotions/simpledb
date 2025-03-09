package com.alivenotions.simpledb.log;

import static com.alivenotions.simpledb.log.LogRecordType.SETSTRING;

import com.alivenotions.simpledb.file.Block;
import com.alivenotions.simpledb.file.Page;
import com.alivenotions.simpledb.transaction.Transaction;

public final class SetStringRecord implements LogRecord {
    private final int txNum;
    private final int offset;
    private final String value;
    private final Block block;

    public SetStringRecord(Page page) {
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
        this.value = page.getString(oldValuePosition);
    }

    @Override
    public int op() {
        return SETSTRING.code();
    }

    @Override
    public int txNum() {
        return txNum;
    }

    // [RecordType] [TxNum] [BlockNum] [Offset] [Value]
    public String toString() {
        return "<SETSTRING %s %s %s %s>".formatted(txNum, block, offset, value);
    }

    @Override
    public void undo(Transaction tx) {
        tx.pin(block);
        // Undo itself should not be logged
        tx.setString(block, offset, value, false);
        tx.unpin(block);
    }

    public static int writeToLog(
            LogManager logManager, int txNum, Block block, int offset, String value) {
        int txPosition = Integer.BYTES;
        int fileNamePosition = txPosition + Integer.BYTES;
        int blockNumPosition = fileNamePosition + Page.maxLength(block.fileName().length());
        int offsetPosition = blockNumPosition + Integer.BYTES;
        int valuePosition = offsetPosition + Integer.BYTES;
        int recordSize = valuePosition + Page.maxLength(value.length());

        byte[] record = new byte[recordSize];
        Page p = Page.newPage(record);
        p.setInt(0, SETSTRING.code());
        p.setInt(txPosition, txNum);
        p.setString(fileNamePosition, block.fileName());
        p.setInt(blockNumPosition, block.number());
        p.setInt(offsetPosition, offset);
        p.setString(valuePosition, value);
        return logManager.append(record);
    }
}
