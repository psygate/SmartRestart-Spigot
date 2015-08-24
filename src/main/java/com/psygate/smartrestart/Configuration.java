/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartrestart;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 *
 * @author florian
 */
public class Configuration {
    /*
     Max-Log-Size: 10k
     # Set the memory limit for a restart.
     Memory-Limit: 90%
     */

    private int logSize;
    private float memoryLimit;
    private boolean memoryLimitRestartEnabled;
    private boolean scheduledRestartEnabled;
    private boolean tickRestartEnabled;
    private final String logsizepattern = "[0-9]+[mkc]?";
    private final String memoryLimitPattern = "[0-9]{2}%";
    private long timeout = TimeUnit.MINUTES.toMillis(30);
    private long forceHours;
    private int tickLowerLimit;
    private long memorySamplePeriod;
    private long tickSamplePeriod;
    private long messageRateLimit;

    public Configuration(SmartRestart start) {
        if (!Pattern.matches(logsizepattern, start.getConfig().getString("Max-Log-Size"))) {
            throw new IllegalArgumentException("Missconfiguration @Max-Log-Size");
        }

        if (!Pattern.matches(memoryLimitPattern, start.getConfig().getString("Memory-Limit"))) {
            throw new IllegalArgumentException("Missconfiguration @Memory-Limit");
        }
        memoryLimitRestartEnabled = start.getConfig().getBoolean("Memory-Limit-Enabled");
        scheduledRestartEnabled = start.getConfig().getBoolean("Scheduled-Restart-Enabled");
        tickRestartEnabled = start.getConfig().getBoolean("Tick-Restart-Enabled");
        memorySamplePeriod = Helper.timeStringAsMillis(start.getConfig().getString("Memory-Sample-Period"));
        tickSamplePeriod = Helper.timeStringAsMillis(start.getConfig().getString("Tick-Sample-Period"));
        messageRateLimit = Helper.timeStringAsMillis(start.getConfig().getString("Message-Rate-Limit"));

        int logmod = 1;
        String logarg = start.getConfig().getString("Max-Log-Size");
        String parseLogStr = null;
        if (logarg.contains("c")) {
            logmod = 100;
            parseLogStr = logarg.replace("c", "");
        } else if (logarg.contains("k")) {
            logmod = 1000;
            parseLogStr = logarg.replace("k", "");
        } else if (logarg.contains("m")) {
            logmod = 1000000;
            parseLogStr = logarg.replace("m", "");
        }

        tickLowerLimit = start.getConfig().getInt("Restart-On-Tick-Below");
        logSize = logmod * Integer.parseInt(parseLogStr);
        memoryLimit = Float.parseFloat(start.getConfig().getString("Memory-Limit").replace("%", "")) / 100.0f;
        timeout = Helper.timeStringAsMillis(start.getConfig().getString("Restart-Timeout"));
        forceHours = Helper.timeStringAsMillis(start.getConfig().getString("Restart-Force-Hours"));
        if (logSize < 0 || memoryLimit < 0 || timeout < 0 || forceHours < 0 || tickLowerLimit < 0) {
            throw new IllegalArgumentException("Configuration parameters cannot be <0");
        }
    }

    public int getLogSize() {
        return logSize;
    }

    public float getMemoryLimit() {
        return memoryLimit;
    }

    public long getTimeout() {
        return timeout;
    }

    public long getForceHours() {
        return forceHours;
    }

    public boolean isMemoryLimitRestartEnabled() {
        return memoryLimitRestartEnabled;
    }

    public boolean isScheduledRestartEnabled() {
        return scheduledRestartEnabled;
    }

    public boolean isTickRestartEnabled() {
        return tickRestartEnabled;
    }

    public int getTickLowerLimit() {
        return tickLowerLimit;
    }

    public long getMemorySamplePeriod() {
        return memorySamplePeriod;
    }

    public long getTickSamplePeriod() {
        return tickSamplePeriod;
    }

    public long getMessageRateLimit() {
        return messageRateLimit;
    }

}
