/*


 The MIT License (MIT)

 Copyright (c) 2015 psygate (http://github.com/psygate)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package com.psygate.smartrestart.runnables;

import com.psygate.smartrestart.data.Record;
import com.psygate.smartrestart.SmartRestart;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author psygate (http://github.com/psygate)
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
