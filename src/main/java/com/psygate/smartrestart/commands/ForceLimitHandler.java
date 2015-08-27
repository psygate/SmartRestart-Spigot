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
 * @author psygate (http://github.com/psygate)
 */
public class ForceLimitHandler implements CommandExecutor {

    private final LinkedList<byte[]> force = new LinkedList<>();
    private int myid = -1;
    private boolean active = false;

    @Override
    public boolean onCommand(final CommandSender cs, Command cmnd, String string, String[] strings) {
        if (!active) {
            myid = Bukkit.getScheduler().scheduleSyncRepeatingTask(SmartRestart.getInstance(), new Runnable() {

                @Override
                public void run() {
                    long start = System.currentTimeMillis();

                    while (MemoryInterface.getUsedMemoryPercent() < SmartRestart.getInstance().getConf().getMemoryLimit() && System.currentTimeMillis() - start < 100) {
                        force.add(new byte[1024 * 1024]);
                    }

                    cs.sendMessage("Allocated: " + force.size() + "mb.");
                    cs.sendMessage(MemoryInterface.getUsedMemoryPercent() + "/" + SmartRestart.getInstance().getConf().getMemoryLimit());
                    cs.sendMessage("Free: " + (MemoryInterface.getFreeMemory() / 1024));
                    cs.sendMessage("Total: " + (MemoryInterface.getMaxMemory() / 1024));
                    cs.sendMessage("Used: " + (MemoryInterface.getUsedMemory() / 1024));
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
