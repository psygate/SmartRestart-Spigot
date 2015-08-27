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

import com.psygate.smartrestart.SmartRestart;
import com.psygate.smartrestart.runnables.Checker;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author psygate (http://github.com/psygate)
 */
public class TickCriteria implements TickingRestartCriteria {

    private long lastWarn = 0;
    private long lastTickWarn = 0;
    private List<Long> ticktimes = new LinkedList<>();

    @Override
    public void tick() {
        ticktimes.add(System.currentTimeMillis());

        if (ticktimes.size() % 200 == 0) {
            //Clear out our data to clear memory.
            Iterator<Long> it = ticktimes.iterator();
            while (it.hasNext()) {
                if (it.next() < SmartRestart.getInstance().getConf().getTickSamplePeriod()) {
                    it.remove();
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public String getReason() {
        return "Tick-Low Limit";
    }

    @Override
    public boolean isLockOutAffected() {
        return true;
    }

    @Override
    public boolean isCriteriaViolated() {
        if (ticktimes.isEmpty() || ticktimes.get(0) > System.currentTimeMillis() - SmartRestart.getInstance().getConf().getTickSamplePeriod()) {
            return false;
        }

        int ticks = 0;

        long timestamp = System.currentTimeMillis();
        for (Long val : ticktimes) {
            if (val > timestamp - SmartRestart.getInstance().getConf().getTickSamplePeriod()) {
                ticks++;
            }
        }
        long ticksRequired = TimeUnit.MILLISECONDS.toSeconds(SmartRestart.getInstance().getConf().getTickSamplePeriod()) * SmartRestart.getInstance().getConf().getTickLowerLimit();
        float ticksWarn = ticksRequired * 1.2f;

        if (ticks < ticksWarn) {
            if (System.currentTimeMillis() - lastTickWarn > SmartRestart.getInstance().getConf().getMessageRateLimit()) {
                float critval = ticks / ((TimeUnit.MILLISECONDS.toSeconds(SmartRestart.getInstance().getConf().getTickSamplePeriod()) * SmartRestart.getInstance().getConf().getTickLowerLimit()) * 1.2f);
                String msg = SmartRestart.PREFIX + ChatColor.RED + "Tick is approaching critical values. (" + critval + ")\n"
                        + "Limit: " + SmartRestart.getInstance().getConf().getTickLowerLimit() + " Current:" + "[" + ticks + "]" + ")\n"
                        + "Required: " + ticksRequired + " Advise restart: " + (ticks < ticksRequired);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("smartstart.notifications")) {
                        p.sendMessage(msg);
                    }
                }

                lastTickWarn = System.currentTimeMillis();
            }
        }
        return ticks < ticksRequired;
    }

    @Override
    public void cancelledByTimeout() {
        if (System.currentTimeMillis() - lastWarn > SmartRestart.getInstance().getConf().getMessageRateLimit()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("smartstart.notifications")) {
                    p.sendMessage(SmartRestart.PREFIX + ChatColor.RED + "Tick restart cancelled by timeout.");
                }
            }

            lastWarn = System.currentTimeMillis();
        }
    }

    @Override
    public String getName() {
        return "tick";
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
