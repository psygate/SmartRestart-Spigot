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
 * @author psygate (http://github.com/psygate)
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
