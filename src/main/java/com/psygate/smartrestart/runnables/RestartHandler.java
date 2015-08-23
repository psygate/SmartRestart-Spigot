/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartrestart.runnables;

import com.psygate.smartrestart.SmartRestart;
import com.psygate.smartrestart.data.RestartCriteria;
import com.psygate.smartrestart.data.TickingRestartCriteria;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class RestartHandler implements Runnable {

    private static final SortedMap<Long, String> limits = new TreeMap<>();

    static {
        limits.put(TimeUnit.MINUTES.toMillis(4), "4 minutes");
        limits.put(TimeUnit.MINUTES.toMillis(3), "3 minutes");
        limits.put(TimeUnit.MINUTES.toMillis(2), "2 minutes");
        limits.put(TimeUnit.MINUTES.toMillis(1), "1 minute");
        limits.put(TimeUnit.SECONDS.toMillis(30), "30 seconds");
        limits.put(TimeUnit.SECONDS.toMillis(20), "20 seconds");
        for (int i = 1; i <= 10; i++) {
            limits.put(TimeUnit.SECONDS.toMillis(i), i + " seconds");
        }
    }

    private final List<RestartCriteria> criteria = new ArrayList<>(10);
    private final List<TickingRestartCriteria> tickcriteria = new ArrayList<>(10);
    private boolean isRestart = false;
    private SortedMap<Long, String> warns = new TreeMap<>();
    private long scheduled = -1;
    private String reason = "";
    private final long restartAfter = TimeUnit.MINUTES.toMillis(5);

    @Override
    public void run() {
        for (TickingRestartCriteria crit : tickcriteria) {
            crit.tick();
        }
        SmartRestart smart = SmartRestart.getInstance();
        Checker checker = smart.getChecker();

        if (checker.getTicks() < 0) {
            return;
        }
        if (!isRestart) {
            for (RestartCriteria crit : criteria) {
                if (crit.isCriteriaViolated()) {
                    if (!crit.LockOutAffected()) {
                        triggerRestart(crit.getReason());
                    } else {
                        if (System.currentTimeMillis() - smart.getLastRestart() < smart.getConf().getTimeout()) {
                            crit.cancelledByTimeout();
                        } else {
                            triggerRestart(crit.getReason());
                        }
                    }
                }
            }
        } else {
            if (!warns.isEmpty() && restartAfter - warns.lastKey() <= (System.currentTimeMillis() - scheduled)) {
                Bukkit.broadcastMessage(SmartRestart.PREFIX + ChatColor.YELLOW + reason + " scheduled restart in " + warns.get(warns.lastKey()) + ".");
                warns.remove(warns.lastKey());
            }

            if (restartAfter <= (System.currentTimeMillis() - scheduled)) {
                Bukkit.broadcastMessage(SmartRestart.PREFIX + ChatColor.RED + "RESTART.");
                SmartRestart.getInstance().saveLastRestart();

                Bukkit.spigot().restart();
            }
        }
    }

    public void triggerRestart(String reason) {
        if (!isRestart) {
            this.reason = reason;
            Bukkit.broadcastMessage(ChatColor.YELLOW + reason + " Scheduled restart in 5 minutes.");
            warns = new TreeMap<>(limits);
            isRestart = true;
            scheduled = System.currentTimeMillis();
        }
    }

    public void addRestartCriteria(TickingRestartCriteria criteria) {
        this.criteria.add(criteria);
        this.tickcriteria.add(criteria);
    }

    public void addRestartCriteria(RestartCriteria criteria) {
        this.criteria.add(criteria);
    }

    public void clearCriteria() {
        this.criteria.clear();
        this.tickcriteria.clear();
    }
}
