/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartrestart.runnables;

import com.psygate.smartrestart.data.Record;
import com.psygate.smartrestart.SmartRestart;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author florian
 */
public class Checker implements Runnable {

    private final SortedMap<Long, Record> records = new TreeMap<>();
    private long lastCall = -1;
    private int ticks = 0;
    private long memoryFree;
    private long memoryUsed;
    private long memoryTotal;
    private long startTimeStamp = -1;

    @Override
    public synchronized void run() {
        if (startTimeStamp < 0) {
            startTimeStamp = System.currentTimeMillis();
        }
        long lastCallDiff = 0;
        if (lastCall != -1) {
            lastCallDiff = System.currentTimeMillis() - lastCall;
        }
        lastCall = System.currentTimeMillis();
        ticks++;

        memoryTotal = Runtime.getRuntime().maxMemory();
        memoryUsed = memoryTotal - Runtime.getRuntime().freeMemory();
        memoryFree = memoryTotal - memoryUsed;
        long timestamp = System.currentTimeMillis();
        records.put(timestamp, new Record(timestamp, memoryFree, memoryUsed, memoryTotal, lastCallDiff));

        while (records.size() > SmartRestart.getInstance().getConf().getLogSize()) {
            records.remove(records.firstKey());
        }
    }

    public synchronized long getLastCall() {
        return lastCall;
    }

    public synchronized void setLastCall(long lastCall) {
        this.lastCall = lastCall;
    }

    public synchronized int getTicks() {
        return ticks;
    }

    public synchronized long getMemoryFree() {
        return memoryFree;
    }

    public synchronized long getMemoryUsed() {
        return memoryUsed;
    }

    public synchronized long getMemoryTotal() {
        return memoryTotal;
    }

    public SortedMap<Long, Record> getRecords() {
        return Collections.unmodifiableSortedMap(records);
    }

    public synchronized long getStartTimeStamp() {
        return startTimeStamp;
    }

}
