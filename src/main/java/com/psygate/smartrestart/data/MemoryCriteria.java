/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author florian
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
    public boolean LockOutAffected() {
        return true;
    }

    @Override
    public boolean isCriteriaViolated() {
        if (memoryLog.isEmpty() || memoryLog.firstKey() > System.currentTimeMillis() - SmartRestart.getInstance().getConf().getTickSamplePeriod()) {
            return false;
        }

        float val = 0;
        SortedMap<Long, Float> subset = memoryLog.tailMap(System.currentTimeMillis() - SmartRestart.getInstance().getConf().getMemorySamplePeriod());
        for (Float f : subset.values()) {
            val += f;
        }

        val /= subset.size();

        return MemoryInterface.getUsedMemoryPercent() >= val;
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
}
