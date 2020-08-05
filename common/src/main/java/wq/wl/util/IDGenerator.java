package wq.wl.util;

import java.util.concurrent.atomic.AtomicLong;

public class IDGenerator {

    public static long generateId() {
        return instance().getId();
    }

    public static IDGenerator instance() {
        return instance;
    }

    public IDGenerator(long nodeId) {
        this.nodeId = nodeId;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    private IDGenerator() {
        this(1L);
    }

    private AtomicLong sequence = new AtomicLong(0);

    private long nodeId;

    private long lastTimestamp = 0L;

    private static IDGenerator instance = new IDGenerator();

    private static final long SEQUENCE_BIT_NUM = 12L;

    private static final long NODE_ID_BIT_NUM = 10L;

    private static final long MAX_SEQUENCE = 4096L;

    private static final long MAX_NODE_ID = 1024L;

    public long getId() {
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp == lastTimestamp) {
            if (sequence.get() == MAX_SEQUENCE) {
                currentTimestamp = tilNextMillis(lastTimestamp);
                sequence = new AtomicLong(0);
            }
        } else {
            sequence = new AtomicLong(0);
        }
        lastTimestamp = currentTimestamp;
        long id = (currentTimestamp << (SEQUENCE_BIT_NUM + SEQUENCE_BIT_NUM))
                | (nodeId << SEQUENCE_BIT_NUM)
                | sequence.getAndIncrement();
        return id;
    }

}
