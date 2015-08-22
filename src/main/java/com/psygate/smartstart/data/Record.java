/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartstart.data;

/**
 *
 * @author florian
 */
public class Record {

    private final long timestamp;
    private final int ticks = 0;
    private final long memoryFree;
    private final long memoryUsed;
    private final long memoryTotal;
    private final long lastCallDiff;

    public Record(long timestamp, long memoryFree, long memoryUsed, long memoryTotal, long lastCallDiff) {
        this.timestamp = timestamp;
        this.memoryFree = memoryFree;
        this.memoryUsed = memoryUsed;
        this.memoryTotal = memoryTotal;
        this.lastCallDiff = lastCallDiff;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getTicks() {
        return ticks;
    }

    public long getMemoryFree() {
        return memoryFree;
    }

    public long getMemoryUsed() {
        return memoryUsed;
    }

    public long getMemoryTotal() {
        return memoryTotal;
    }

    public long getLastCallDiff() {
        return lastCallDiff;
    }

}
