/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartstart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

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
    private boolean isRestart = false;
    private SortedMap<Long, String> warns = new TreeMap<>();
    private long scheduled = -1;
    private long restartAfter = TimeUnit.MINUTES.toMillis(5);

    @Override
    public void run() {
        Checker checker = SmartStart.getInstance().getChecker();

        if (checker.getTicks() < 0) {
            return;
        }

        if (!isRestart) {
            if ((float) checker.getMemoryUsed() / (float) checker.getMemoryTotal() >= SmartStart.getInstance().getConf().getMemoryLimit()) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "Memory limit reached. Scheduled restart in 5 minutes.");
                warns = new TreeMap<>(limits);
                isRestart = true;
                scheduled = System.currentTimeMillis();
            }
        } else {
            if (!warns.isEmpty() && restartAfter - warns.lastKey() <= (System.currentTimeMillis() - scheduled)) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + "Memory limit reached. Scheduled restart in " + warns.get(warns.lastKey()) + ".");
                warns.remove(warns.lastKey());
            }

            if (restartAfter <= (System.currentTimeMillis() - scheduled)) {
                Bukkit.broadcastMessage(ChatColor.RED + "RESTART.");

                Bukkit.spigot().restart();
            }
        }
    }
}
