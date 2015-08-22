/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartrestart.commands;

import com.psygate.smartrestart.MemoryInterface;
import com.psygate.smartrestart.SmartRestart;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author florian
 */
public class ForceLimitHandler implements CommandExecutor {

    private LinkedList<byte[]> force = new LinkedList<>();
    private int myid = -1;
    private boolean active = false;

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (!active) {
            myid = Bukkit.getScheduler().scheduleSyncRepeatingTask(SmartRestart.getInstance(), new Runnable() {

                @Override
                public void run() {
                    long start = System.currentTimeMillis();

//                if (ratio >= SmartRestart.getInstance().getConf().getMemoryLimit()) {
//                    force.clear();
//                    Bukkit.getScheduler().cancelTask(myid);
//                    return;
//                }
                    while (MemoryInterface.getUsedMemoryPercent() < SmartRestart.getInstance().getConf().getMemoryLimit() && System.currentTimeMillis() - start < 100) {
                        force.add(new byte[1024 * 1024]);

                    }

                }
            }, 1, 1);
            cs.sendMessage(SmartRestart.PREFIX + ChatColor.GOLD + "Forcing memory exhaustion.");
            active = true;
        } else {
            force.clear();
            cs.sendMessage(SmartRestart.PREFIX + ChatColor.GOLD + "Un-Forcing memory exhaustion.");
            active = false;
        }

        return true;
    }

}
