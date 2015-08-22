/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartstart;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author florian
 */
public class Checker implements Runnable {

    private Map<Long, Record> records = new HashMap<>();
    private long lastCall = -1;
    private int ticks = 0;
    private long memoryFree;
    private long memoryUsed;
    private long memoryTotal;

    @Override
    public synchronized void run() {
        lastCall = System.currentTimeMillis();
        ticks++;

        memoryTotal = Runtime.getRuntime().maxMemory();
        memoryUsed = memoryTotal - Runtime.getRuntime().freeMemory();
        memoryFree = memoryTotal - memoryUsed;
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

}
