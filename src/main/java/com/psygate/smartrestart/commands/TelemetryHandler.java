/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartrestart.commands;

import com.psygate.smartrestart.runnables.Checker;
import com.psygate.smartrestart.data.Record;
import com.psygate.smartrestart.SmartRestart;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
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

    private static final String TICK_TOKEN = "$TICKS$";
    private static final String MEMORY_USAGE_TOKEN = "$MEMORUSAGE$";
    private static final String FREE_MEMORY_TOKEN = "$FREEMEMORY$";
    private static final String AVG_MILLIS_TOKEN = "$AVGMILLISPERTICK$";
    private static final StringBuilder telemetrytemplate = new StringBuilder();
    private static final String PREFIX = "[Telemetry]";

    static {
        telemetrytemplate.append(PREFIX).append(" Telemetry @Tick($TICKS$):\n");
        telemetrytemplate.append("\tMemory Usage: $MEMORUSAGE$%\n");
        telemetrytemplate.append("\tFree Memory: $FREEMEMORY$%\n");
        telemetrytemplate.append("\tAvg. Millis / Tick: $AVGMILLISPERTICK$ms");
    }

    private final Map<UUID, Long> receivers = new HashMap<>();

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

                cs.sendMessage(ChatColor.GREEN + PREFIX + "Telemetry enabled.");
            } else {
                receivers.remove(p.getUniqueId());
                cs.sendMessage(ChatColor.YELLOW + PREFIX + "Telemetry disabled.");
            }
        }

        return true;
    }

    @Override
    public void run() {
        Checker checker = SmartRestart.getInstance().getChecker();

        if (checker.getTicks() < 0) {
            return;
        }

        float memoryusage = ((float) checker.getMemoryUsed() / (float) checker.getMemoryTotal()) * 100;
        float freememory = ((float) checker.getMemoryFree() / (float) checker.getMemoryTotal()) * 100;

        //Build message.
        final StringBuilder nowmsg = new StringBuilder(telemetrytemplate);
        replace(nowmsg, TICK_TOKEN, Integer.toString(checker.getTicks()));
        replace(nowmsg, MEMORY_USAGE_TOKEN, Float.toString(memoryusage));
        replace(nowmsg, FREE_MEMORY_TOKEN, Float.toString(freememory));
        replace(nowmsg, AVG_MILLIS_TOKEN, getAvgTimePerTick(checker.getRecords()));

        for (Map.Entry<UUID, Long> en : receivers.entrySet()) {
            if (checker.getTicks() % en.getValue() == 0) {
                Player p = Bukkit.getPlayer(en.getKey());
                if (p == null) {
                    continue;
                }
                p.sendMessage(nowmsg.toString());
            }
        }
    }

    private void replace(StringBuilder builder, String token, String value) {
        builder.replace(builder.indexOf(token), builder.indexOf(token) + token.length(), value);
    }

    private String getAvgTimePerTick(SortedMap<Long, Record> records) {
        long sum = 0;
        for (Map.Entry<Long, Record> en : records.entrySet()) {
            sum += en.getValue().getLastCallDiff();
        }

        long globalavg = sum / records.size();

        sum = 0;
        Set<Map.Entry<Long, Record>> subset = records.tailMap(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(15)).entrySet();

        for (Map.Entry<Long, Record> en : subset) {
            sum += en.getValue().getLastCallDiff();
        }

        long min15avg = sum / subset.size();

        sum = 0;
        subset = records.tailMap(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(10)).entrySet();
        for (Map.Entry<Long, Record> en : subset) {
            sum += en.getValue().getLastCallDiff();
        }

        long min10avg = sum / subset.size();

        sum = 0;
        subset = records.tailMap(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5)).entrySet();

        for (Map.Entry<Long, Record> en : subset) {
            sum += en.getValue().getLastCallDiff();
        }

        long min5avg = sum / subset.size();

        sum = 0;
        subset = records.tailMap(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1)).entrySet();
        for (Map.Entry<Long, Record> en : subset) {
            sum += en.getValue().getLastCallDiff();
        }

        long min1avg = sum / subset.size();

        return globalavg + " "
                + ChatColor.GRAY + min15avg + " "
                + ChatColor.YELLOW + min10avg + " "
                + ChatColor.GOLD + min5avg + " "
                + ChatColor.GREEN + min1avg;
    }
}
