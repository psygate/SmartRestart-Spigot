/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartstart;

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
    private final String logsizepattern = "[0-9]+[mkc]?";
    private final String memoryLimitPattern = "[0-9]{2}%";

    public Configuration(SmartStart start) {
        if (!Pattern.matches(logsizepattern, start.getConfig().getString("Max-Log-Size"))) {
            throw new IllegalArgumentException("Missconfiguration @Max-Log-Size");
        }

        if (!Pattern.matches(memoryLimitPattern, start.getConfig().getString("Memory-Limit"))) {
            throw new IllegalArgumentException("Missconfiguration @Memory-Limit");
        }

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

        logSize = logmod * Integer.parseInt(parseLogStr);
        memoryLimit = Float.parseFloat(start.getConfig().getString("Memory-Limit").replace("%", "")) / 100.0f;

        if (logSize < 0 || memoryLimit < 0) {
            throw new IllegalArgumentException("Configuration parameters cannot be <0");
        }
    }

    public int getLogSize() {
        return logSize;
    }

    public float getMemoryLimit() {
        return memoryLimit;
    }

}
