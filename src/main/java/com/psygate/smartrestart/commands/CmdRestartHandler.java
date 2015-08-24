/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartrestart.commands;

import com.psygate.smartrestart.SmartRestart;
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
 * @author florian
 */
public class CmdRestartHandler implements CommandExecutor {

    private CommandRestartCriteria crc = new CommandRestartCriteria();

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        crc.requested = true;
        cs.sendMessage(SmartRestart.PREFIX + ChatColor.GREEN + "Restart scheduled.");
        return true;
    }

    public RestartCriteria getCrc() {
        return crc;
    }

    private class CommandRestartCriteria implements RestartCriteria {

        private boolean requested = false;

        public void setRequested(boolean requested) {
            this.requested = requested;
        }

        @Override
        public String getReason() {
            return "Requested-by-OP";
        }

        @Override
        public boolean LockOutAffected() {
            return false;
        }

        @Override
        public boolean isCriteriaViolated() {
            return requested;
        }

        @Override
        public void cancelledByTimeout() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getName() {
            return "OP-Requested";
        }
    }
}
