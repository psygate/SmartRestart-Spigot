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
import com.psygate.smartrestart.data.EventType;
import com.psygate.smartrestart.data.RestartCriteria;
import com.sun.corba.se.impl.activation.CommandHandler;
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
public class RestartCommand implements CommandExecutor, RestartCriteria {

    private boolean violated = false;
    private long restartTime = TimeUnit.MINUTES.toMillis(10);

    @Override
    public String getReason() {
        return "Administrator Request";
    }

    @Override
    public boolean isLockOutAffected() {
        return false;
    }

    @Override
    public boolean isCriteriaViolated() {
        return violated;
    }

    @Override
    public void cancelledByTimeout() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getName() {
        return "Administrator-Restart";
    }

    @Override
    public EventType getType() {
        return EventType.RESTART;
    }

    @Override
    public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
        if (arg3.length > 1) {
            arg0.sendMessage(SmartRestart.PREFIX + ChatColor.RED + "Too many arguments.");
        } else if (arg3.length < 1) {
            violated = true;
        } else {
            try {
                long restart = TimeUnit.MINUTES.toMillis(Integer.parseInt(arg3[0]));
                if (restart <= 0) {
                    arg0.sendMessage(SmartRestart.PREFIX + ChatColor.RED + "Invalid value \"" + arg3[0] + "\"");
                } else {
                    restartTime = restart;
                    violated = true;
                    arg0.sendMessage(SmartRestart.PREFIX + ChatColor.YELLOW + "Restart is set.");

                }
            } catch (NumberFormatException ex) {
                arg0.sendMessage(SmartRestart.PREFIX + ChatColor.RED + "Cannot parse \"" + arg3[0] + "\"");
            }
        }
        return true;
    }

    @Override
    public long restartAfterMillis() {
        return restartTime;
    }
}
