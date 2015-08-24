/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.psygate.smartrestart.commands;

import com.psygate.smartrestart.SmartRestart;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author florian
 */
public class ReloadHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        SmartRestart.getInstance().reloadConfig();
        cs.sendMessage(SmartRestart.PREFIX + ChatColor.GREEN + "Configuration reloaded.");
        return true;
    }

}
