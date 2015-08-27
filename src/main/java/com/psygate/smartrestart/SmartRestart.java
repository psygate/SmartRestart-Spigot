/*


 The MIT License (MIT)

 Copyright (c) 2015 psygate (http://github.com/psygate)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package com.psygate.smartrestart;

import com.psygate.smartrestart.commands.RestartCommand;
import com.psygate.smartrestart.runnables.RestartHandler;
import com.psygate.smartrestart.runnables.Checker;
import com.psygate.smartrestart.commands.ForceLimitHandler;
import com.psygate.smartrestart.commands.ForceTickHandler;
import com.psygate.smartrestart.commands.ReloadHandler;
import com.psygate.smartrestart.commands.StopCommand;
import com.psygate.smartrestart.commands.TelemetryHandler;
import com.psygate.smartrestart.data.MemoryCriteria;
import com.psygate.smartrestart.data.ScheduledCriteria;
import com.psygate.smartrestart.data.TickCriteria;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author psygate (http://github.com/psygate)
 */
public class SmartRestart extends JavaPlugin {

    private static SmartRestart instance = null;
    private Checker checker;
    private TelemetryHandler telemetryhandler = new TelemetryHandler();
    private RestartHandler restarthandler = new RestartHandler();
    private RestartCommand restartcmd = new RestartCommand();
    private StopCommand stopcmd = new StopCommand();
    private Map<Runnable, Integer> runnables = new HashMap<>();

    private Configuration conf;
    public final static String PREFIX = "[SmartRestart]";

    public SmartRestart() {
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
        Bukkit.getPluginCommand("srtelemetry").setExecutor(telemetryhandler);
        Bukkit.getPluginCommand("srforcelimit").setExecutor(new ForceLimitHandler());
        Bukkit.getPluginCommand("srforcetick").setExecutor(new ForceTickHandler());
        Bukkit.getPluginCommand("srreload").setExecutor(new ReloadHandler());
        Bukkit.getPluginCommand("srrestart").setExecutor(restartcmd);
        Bukkit.getPluginCommand("srstop").setExecutor(stopcmd);
        registerRunnable(telemetryhandler, 1, 1);
        registerRunnable(restarthandler, 1, 1);

        if (conf.isMemoryLimitRestartEnabled()) {
            restarthandler.addRestartCriteria(new MemoryCriteria());
        }

        if (conf.isScheduledRestartEnabled()) {
            restarthandler.addRestartCriteria(new ScheduledCriteria());
        }

        if (conf.isTickRestartEnabled()) {
            restarthandler.addRestartCriteria(new TickCriteria());
        }

        restarthandler.addRestartCriteria(restartcmd);
        restarthandler.addRestartCriteria(stopcmd);

        //This makes spigot mandatory for this plugin.
        Bukkit.spigot().getConfig();
    }

    public void reloadConfiguration() {
        conf = new Configuration(this);
        restarthandler.clearCriteria();
        if (conf.isMemoryLimitRestartEnabled()) {
            restarthandler.addRestartCriteria(new MemoryCriteria());
        }

        if (conf.isScheduledRestartEnabled()) {
            restarthandler.addRestartCriteria(new ScheduledCriteria());
        }

        if (conf.isTickRestartEnabled()) {
            restarthandler.addRestartCriteria(new TickCriteria());
        }
    }

    public Checker getChecker() {
        return checker;
    }

    public static SmartRestart getInstance() {
        return instance;
    }

    public Configuration getConf() {
        return conf;
    }

//    public void triggerRestart(String reason) {
//        restarthandler.triggerRestart(reason);
//    }
    private long lastRestart = -1;

    public long getLastRestart() {
        if (lastRestart < 0) {
            File loadable = new File(SmartRestart.getInstance().getDataFolder(), "lastRestart.dat");
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
        File loadable = new File(SmartRestart.getInstance().getDataFolder(), "lastRestart.dat");
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(loadable, false))) {
            out.writeLong(System.currentTimeMillis());
        } catch (Exception e) {
            //Pass
        }
    }

    private void registerRunnable(Runnable handler, int delay, int tick) {
        int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, handler, delay, tick);

        runnables.put(handler, id);
    }
}
