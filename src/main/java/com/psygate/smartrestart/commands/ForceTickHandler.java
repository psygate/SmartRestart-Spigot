/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartrestart.commands;

import com.psygate.smartrestart.SmartRestart;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author florian
 */
public class ForceTickHandler implements CommandExecutor {

    LinkedList<byte[]> force = new LinkedList<>();
    int myid = -1;
    boolean active = false;

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (!active) {
            myid = Bukkit.getScheduler().scheduleSyncRepeatingTask(SmartRestart.getInstance(), new Runnable() {

                @Override
                public void run() {
                    long lag = TimeUnit.SECONDS.toMillis(1) / (SmartRestart.getInstance().getConf().getTickLowerLimit() - 2);

                    try {
                        Thread.sleep(lag);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ForceTickHandler.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }, 1, 1);
            cs.sendMessage(SmartRestart.PREFIX + ChatColor.GOLD + "Tick plunging forced.");
            active = true;
        } else {
            Bukkit.getScheduler().cancelTask(myid);
            cs.sendMessage(SmartRestart.PREFIX + ChatColor.GOLD + "Tick plunging un-forced.");
            active = false;
        }
        return true;
    }

}
