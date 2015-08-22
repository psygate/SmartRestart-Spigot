/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author florian
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
    public boolean LockOutAffected() {
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
}
