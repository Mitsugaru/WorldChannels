package com.mitsugaru.worldchannels.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitsugaru.worldchannels.chat.Channel;
import com.mitsugaru.worldchannels.config.ConfigHandler;
import com.mitsugaru.worldchannels.config.WorldConfig;
import com.mitsugaru.worldchannels.services.ICommand;

public abstract class AbstractChannelCommand implements ICommand {

    protected Channel parseChannel(CommandSender sender, String param,
            ConfigHandler configHandler) {
        Channel channel = null;
        if(param.contains(":")) {
            final String[] split = param.split(":");
            // parse for world
            final String worldName = split[0];
            final String channelName = split[1];
            final WorldConfig conf = configHandler.getWorldConfig(worldName);
            channel = conf.getChannel(channelName);
        } else {
            // try local
            final WorldConfig conf = configHandler
                    .getWorldConfig(((Player) sender).getWorld().getName());
            channel = conf.getChannel(param);
            if(channel == null) {
                // try and get it from global
                for(Channel c : configHandler.getGlobalChannels()) {
                    if(c.getName().equalsIgnoreCase(param)) {
                        channel = c;
                    }
                }
            }
        }
        return channel;
    }

    /**
     * Attempts to look up full name based on who's on the server Given a
     * partial name
     * 
     * @author Frigid, edited by Raphfrk and petteyg359
     */
    public String expandName(String Name) {
        int m = 0;
        String Result = "";
        for(int n = 0; n < Bukkit.getServer().getOnlinePlayers().length; n++) {
            String str = Bukkit.getServer().getOnlinePlayers()[n].getName();
            if(str.matches("(?i).*" + Name + ".*")) {
                m++;
                Result = str;
                if(m == 2) {
                    return null;
                }
            }
            if(str.equalsIgnoreCase(Name))
                return str;
        }
        if(m == 1)
            return Result;
        if(m > 1) {
            return null;
        }
        return Name;
    }
}
