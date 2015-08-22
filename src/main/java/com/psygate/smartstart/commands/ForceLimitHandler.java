/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartstart.commands;

import com.psygate.smartstart.SmartStart;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author florian
 */
public class ForceLimitHandler implements CommandExecutor {

    LinkedList<byte[]> force = new LinkedList<>();
    int myid = -1;

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        myid = Bukkit.getScheduler().scheduleSyncRepeatingTask(SmartStart.getInstance(), new Runnable() {

            @Override
            public void run() {
                float memoryTotal = Runtime.getRuntime().maxMemory();
                float memoryUsed = memoryTotal - Runtime.getRuntime().freeMemory();
                float ratio = memoryUsed / memoryTotal;
                long start = System.currentTimeMillis();

                if (ratio >= SmartStart.getInstance().getConf().getMemoryLimit()) {
                    force.clear();
                    Bukkit.getScheduler().cancelTask(myid);
                    return;
                }

                while (ratio < SmartStart.getInstance().getConf().getMemoryLimit() && System.currentTimeMillis() - start < 100) {
                    force.add(new byte[1024 * 1024]);
                    memoryTotal = Runtime.getRuntime().maxMemory();
                    memoryUsed = memoryTotal - Runtime.getRuntime().freeMemory();
                    ratio = memoryUsed / memoryTotal;
                }

            }
        }, 1, 1);

        return true;
    }

}
