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

import com.psygate.smartrestart.MemoryInterface;
import com.psygate.smartrestart.SmartRestart;
import com.psygate.smartrestart.runnables.Checker;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author psygate (http://github.com/psygate)
 */
public class MemoryCriteria implements TickingRestartCriteria {

    private long lastWarn = 0;
    private SortedMap<Long, Float> memoryLog = new TreeMap<>();

    @Override
    public void tick() {
        memoryLog.put(System.currentTimeMillis(), MemoryInterface.getUsedMemoryPercent());

        if (memoryLog.size() % 200 == 0) {
            //Clear out our data to clear memory.
            Iterator<Long> it = memoryLog.keySet().iterator();
            while (it.hasNext()) {
                if (it.next() < SmartRestart.getInstance().getConf().getMemorySamplePeriod()) {
                    it.remove();
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public String getReason() {
        return "Memory Limit";
    }

    @Override
    public boolean isLockOutAffected() {
        return true;
    }

    @Override
    public boolean isCriteriaViolated() {
        if (memoryLog.isEmpty() || memoryLog.firstKey() > System.currentTimeMillis() - SmartRestart.getInstance().getConf().getMemorySamplePeriod()) {
            return false;
        }

        float val = 0;
        SortedMap<Long, Float> subset = memoryLog.tailMap(System.currentTimeMillis() - SmartRestart.getInstance().getConf().getMemorySamplePeriod());
        for (Float f : subset.values()) {
            val += f;
        }

        val /= subset.size();

        return val >= SmartRestart.getInstance().getConf().getMemoryLimit();
    }

    @Override
    public void cancelledByTimeout() {
        if (System.currentTimeMillis() - lastWarn > TimeUnit.SECONDS.toMillis(30)) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("smartstart.notifications")) {
                    p.sendMessage(SmartRestart.PREFIX + ChatColor.RED + "Memory restart cancelled by timeout.");
                }
            }

            lastWarn = System.currentTimeMillis();
        }
    }

    @Override
    public String getName() {
        return "memory";
    }

    @Override
    public EventType getType() {
        return EventType.RESTART;
    }

    @Override
    public long restartAfterMillis() {
        return TimeUnit.MINUTES.toMillis(5);
    }
}
