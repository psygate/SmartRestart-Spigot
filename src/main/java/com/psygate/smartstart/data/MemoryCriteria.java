/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartstart.data;

import com.psygate.smartstart.SmartStart;
import com.psygate.smartstart.runnables.Checker;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author florian
 */
public class MemoryCriteria implements RestartCriteria {

    private long lastWarn = 0;

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
        Checker checker = SmartStart.getInstance().getChecker();
        float currentratio = (float) checker.getMemoryUsed() / (float) checker.getMemoryTotal();

        return currentratio >= SmartStart.getInstance().getConf().getMemoryLimit();
    }

    @Override
    public void cancelledByTimeout() {
        if (System.currentTimeMillis() - lastWarn > TimeUnit.SECONDS.toMillis(30)) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("smartstart.notifications")) {
                    p.sendMessage(SmartStart.PREFIX + ChatColor.RED + "Memory restart cancelled by timeout.");
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
