package com.mitsugaru.worldchannels.commands;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.chat.ChannelManager;
import com.mitsugaru.worldchannels.config.ConfigHandler;
import com.mitsugaru.worldchannels.config.localize.Flag;
import com.mitsugaru.worldchannels.config.localize.LocalString;
import com.mitsugaru.worldchannels.config.localize.Localizer;
import com.mitsugaru.worldchannels.permissions.PermissionNode;
import com.mitsugaru.worldchannels.services.ICommand;

public class ReloadCommand implements ICommand {

    @Override
    public boolean execute(WorldChannels plugin, CommandSender sender,
            Command command, String label, String[] args) {
        final Map<Flag, String> info = new EnumMap<Flag, String>(Flag.class);
        info.put(Flag.TAG, WorldChannels.TAG);
        if(sender.hasPermission(PermissionNode.ADMIN.getNode())) {
            plugin.getModuleForClass(ChannelManager.class).clearChannels();
            plugin.getModuleForClass(ConfigHandler.class).reloadConfigs();
            sender.sendMessage(Localizer.parseString(LocalString.RELOAD_CONFIG,
                    info));
        } else {
            info.put(Flag.EXTRA, PermissionNode.ADMIN.getNode());
            sender.sendMessage(Localizer.parseString(
                    LocalString.PERMISSION_DENY, info));
        }
        return true;
    }

}
