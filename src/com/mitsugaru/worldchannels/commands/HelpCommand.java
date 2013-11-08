package com.mitsugaru.worldchannels.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.config.localize.LocalString;
import com.mitsugaru.worldchannels.config.localize.Localizer;
import com.mitsugaru.worldchannels.permissions.PermissionNode;
import com.mitsugaru.worldchannels.services.ICommand;

public class HelpCommand implements ICommand {

    @Override
    public boolean execute(WorldChannels plugin, CommandSender sender,
            Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.BLUE + "==========" + ChatColor.GOLD
                + "WorldChannels" + ChatColor.BLUE + "==========");
        sender.sendMessage(Localizer.parseString(LocalString.HELP_HELP, null));
        if(sender.hasPermission(PermissionNode.ADMIN.getNode())) {
            sender.sendMessage(Localizer.parseString(
                    LocalString.HELP_ADMIN_RELOAD, null));
        }
        if(sender.hasPermission(PermissionNode.SHOUT.getNode())) {
            sender.sendMessage(Localizer.parseString(LocalString.HELP_SHOUT,
                    null));
        }
        if(sender.hasPermission(PermissionNode.OBSERVE.getNode())) {
            sender.sendMessage(Localizer.parseString(LocalString.HELP_OBSERVE,
                    null));
        }
        sender.sendMessage(Localizer
                .parseString(LocalString.HELP_VERSION, null));
        return true;
    }

}
