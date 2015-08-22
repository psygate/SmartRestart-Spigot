/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartstart;

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
    private Configuration conf;

    public SmartStart() {
        instance = this;
    }

    @Override
    public void onEnable() {
        instance = this;
        getConfig().options().copyDefaults(true);
        saveConfig();

        checker = new Checker();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, checker, 1, 1);
        Bukkit.getPluginCommand("telemetry").setExecutor(telemetryhandler);
        Bukkit.getPluginCommand("forcelimit").setExecutor(new ForceLimitHandler());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, telemetryhandler, 1, 1);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new RestartHandler(), 1, 1);
        conf = new Configuration(this);
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
}
