package com.alivenotions.simpledb.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.alivenotions.simpledb.file.Block;
import com.alivenotions.simpledb.server.SimpleDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionTest {
    private SimpleDB db;

    @BeforeEach
    void setUp() {
        db = new SimpleDB("testdb", 400, 3);
    }

    @Test
    void transaction_can_pin_and_commit_changes() {
        Transaction transaction = db.beginTransaction();
        Block block = Block.of("testfile", 1);
        transaction.pin(block);
        transaction.setInt(block, 80, 1, false);
        transaction.setString(block, 40, "one", false);
        transaction.commit();

        assertEquals(1, transaction.getInt(block, 80));
        assertEquals("one", transaction.getString(block, 40));
    }

    @Test
    void transactions_maintain_isolation() {
        Transaction tx1 = db.beginTransaction();
        Transaction tx2 = db.beginTransaction();
        Transaction tx3 = db.beginTransaction();

        Block block = Block.of("testfile", 1);
        tx1.pin(block);
        tx1.setInt(block, 80, 1, false);
        tx1.setString(block, 40, "one", false);
        tx1.commit();

        tx2.pin(block);
        int val = tx2.getInt(block, 80);
        String str = tx2.getString(block, 40);
        assertEquals(1, val);
        assertEquals("one", str);

        int newVal = val + 1;
        String newStr = str + "!";
        tx2.setInt(block, 80, newVal, false);
        tx2.setString(block, 40, newStr, false);

        tx3.pin(block);
        // Transaction 3 should not see the changes from tx2
        assertEquals(str, tx3.getString(block, 40));
        assertEquals(val, tx3.getInt(block, 80));

        tx2.commit();

        Transaction tx4 = db.beginTransaction();
        tx4.pin(block);
        // Transaction 4 should see the changes from tx2
        assertEquals(newStr, tx4.getString(block, 40));
        assertEquals(newVal, tx4.getInt(block, 80));

        tx3.setInt(block, 80, 9999, true);

        assertEquals(9999, tx3.getInt(block, 80));
        tx3.rollback();
        assertEquals(newVal, tx3.getInt(block, 80));

        tx4.commit();
    }
}
