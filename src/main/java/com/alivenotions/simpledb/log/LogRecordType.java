package com.alivenotions.simpledb.log;

public enum LogRecordType {
    CHECKPOINT(0),
    START(1),
    COMMIT(2),
    ROLLBACK(3),
    SETINT(4),
    SETSTRING(5);

    private final int code;

    LogRecordType(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static LogRecordType fromCode(int code) {
        switch (code) {
            case 0:
                return CHECKPOINT;
            case 1:
                return START;
            case 2:
                return COMMIT;
            case 3:
                return ROLLBACK;
            case 4:
                return SETINT;
            case 5:
                return SETSTRING;
            default:
                throw new IllegalArgumentException("Invalid log record type code: " + code);
        }
    }
}
