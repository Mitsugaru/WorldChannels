package com.mitsugaru.worldchannels.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.services.ICommand;

public class VersionCommand implements ICommand {
    
    private final static String bar = "======================";

    @Override
    public boolean execute(WorldChannels plugin, CommandSender sender,
            Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.BLUE + bar + "=====");
        sender.sendMessage(ChatColor.GREEN + "WorldChannels v"
                + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GREEN + "Coded by Mitsugaru");
        sender.sendMessage(ChatColor.BLUE + "===========" + ChatColor.GRAY
                + "Config" + ChatColor.BLUE + "===========");
        return true;
    }

}
