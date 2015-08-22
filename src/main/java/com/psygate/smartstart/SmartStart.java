/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartstart;

import com.psygate.smartstart.runnables.RestartHandler;
import com.psygate.smartstart.runnables.Checker;
import com.psygate.smartstart.commands.TelemetryHandler;
import com.psygate.smartstart.commands.ForceLimitHandler;
import com.psygate.smartstart.commands.ForceTickHandler;
import com.psygate.smartstart.data.MemoryCriteria;
import com.psygate.smartstart.data.ScheduledCriteria;
import com.psygate.smartstart.data.TickCriteria;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author florian
 */
public class SmartStart extends JavaPlugin {

    private static SmartStart instance = null;
    private Checker checker;
    private TelemetryHandler telemetryhandler = new TelemetryHandler();
    private RestartHandler restarthandler = new RestartHandler();
    private Configuration conf;
    public final static String PREFIX = "[SmartStart]";

    public SmartStart() {
        instance = this;
    }

    @Override
    public void onEnable() {
        instance = this;
        conf = new Configuration(this);
        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);

        saveConfig();

        checker = new Checker();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, checker, 1, 1);
        Bukkit.getPluginCommand("telemetry").setExecutor(telemetryhandler);
        Bukkit.getPluginCommand("forcelimit").setExecutor(new ForceLimitHandler());
        Bukkit.getPluginCommand("forcetick").setExecutor(new ForceTickHandler());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, telemetryhandler, 1, 1);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, restarthandler, 1, 1);
        if (conf.isMemoryLimitRestartEnabled()) {
            restarthandler.addRestartCriteria(new MemoryCriteria());
        }

        if (conf.isScheduledRestartEnabled()) {
            restarthandler.addRestartCriteria(new ScheduledCriteria());
        }

        if (conf.isTickRestartEnabled()) {
            restarthandler.addRestartCriteria(new TickCriteria());
        }

        //This makes spigot mandatory for this plugin.
        Bukkit.spigot().getConfig();
    }

    public Checker getChecker() {
        return checker;
    }

    public static SmartStart getInstance() {
        return instance;
    }

    public Configuration getConf() {
        return conf;
    }

    public void triggerRestart(String reason) {
        restarthandler.triggerRestart(reason);
    }

    private long lastRestart = -1;

    public long getLastRestart() {
        if (lastRestart < 0) {
            File loadable = new File(SmartStart.getInstance().getDataFolder(), "lastRestart.dat");
            if (!loadable.exists()) {
                lastRestart = 0;
            } else {
                try (DataInputStream reader = new DataInputStream(new FileInputStream(loadable))) {
                    lastRestart = reader.readLong();
                } catch (Exception e) {
                    lastRestart = 0;
                }
            }
        }

        return lastRestart;
    }

    public void saveLastRestart() {
        lastRestart = System.currentTimeMillis();
        File loadable = new File(SmartStart.getInstance().getDataFolder(), "lastRestart.dat");
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(loadable, false))) {
            out.writeLong(System.currentTimeMillis());
        } catch (Exception e) {
            //Pass
        }
    }
}
