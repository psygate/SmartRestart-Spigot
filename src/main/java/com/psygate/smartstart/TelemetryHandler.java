/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartstart;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftArt;
import org.bukkit.entity.Player;

/**
 *
 * @author florian
 */
public class TelemetryHandler implements CommandExecutor, Runnable {

    private Map<UUID, Long> receivers = new HashMap<>();
    private String prefix = "[Telemetry]";

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage(ChatColor.RED + "Sry. Not yet implemented.");
        } else {
            Player p = (Player) cs;
            if (!receivers.containsKey(p.getUniqueId())) {
                long ticks = 20;
                if (args.length > 0) {
                    try {
                        ticks = Long.parseLong(args[0]);
                    } catch (NumberFormatException e) {
                        cs.sendMessage(ChatColor.RED + "Not a valid number. [" + args[0] + "]");
                    }
                }
                receivers.put(p.getUniqueId(), ticks);

                cs.sendMessage(ChatColor.GREEN + prefix + "Telemetry enabled.");
            } else {
                receivers.remove(p.getUniqueId());
                cs.sendMessage(ChatColor.YELLOW + prefix + "Telemetry disabled.");
            }
        }

        return true;
    }

    @Override
    public void run() {
        Checker checker = SmartStart.getInstance().getChecker();

        if (checker.getTicks() < 0) {
            return;
        }

        float memoryusage = ((float) checker.getMemoryUsed() / (float) checker.getMemoryTotal()) * 100;
        float freememory = ((float) checker.getMemoryFree() / (float) checker.getMemoryTotal()) * 100;

        for (Map.Entry<UUID, Long> en : receivers.entrySet()) {
            if (checker.getTicks() % en.getValue() == 0) {
                Player p = Bukkit.getPlayer(en.getKey());
                if (p == null) {
                    continue;
                }
                p.sendMessage(prefix + " Telemetry @Tick(" + checker.getTicks() + "):");
                p.sendMessage(prefix + " Memory Usage: " + memoryusage + "%");
                p.sendMessage(prefix + " Free Memory: " + freememory + "%");
            }
        }
    }

}
