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
package com.psygate.smartrestart.data;

/**
 *
 * @author psygate (http://github.com/psygate)
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
