/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartrestart;

/**
 *
 * @author florian
 */
public class MemoryInterface {

    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public static long getUsedMemory() {
        return getMaxMemory() - Runtime.getRuntime().freeMemory();
    }

    public static long getFreeMemory() {
        return getMaxMemory() - getUsedMemory();
    }

    public static float getMaxMemoryPercent() {
        return Runtime.getRuntime().maxMemory();
    }

    public static float getUsedMemoryPercent() {
        float max = getMaxMemory();
        float used = getUsedMemory();
        return used / max;
    }

    public static float getFreeMemoryPercent() {
        float max = getMaxMemory();
        float free = getFreeMemory();
        return free / max;
    }
}
