package com.mitsugaru.worldchannels.commands;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.chat.Channel;
import com.mitsugaru.worldchannels.config.ConfigHandler;
import com.mitsugaru.worldchannels.config.localize.Flag;
import com.mitsugaru.worldchannels.config.localize.LocalString;
import com.mitsugaru.worldchannels.config.localize.Localizer;

public class LeaveCommand extends AbstractChannelCommand {

    @Override
    public boolean execute(WorldChannels plugin, CommandSender sender,
            Command command, String label, String[] args) {
        final Map<Flag, String> info = new EnumMap<Flag, String>(Flag.class);
        info.put(Flag.TAG, WorldChannels.TAG);
        if(!(sender instanceof Player)) {
            sender.sendMessage(Localizer.parseString(LocalString.NO_CONSOLE,
                    info));
        } else {
            try {
                final Channel channel = parseChannel(sender, args[0],
                        plugin.getModuleForClass(ConfigHandler.class));
                if(channel != null) {
                    if(sender.hasPermission(channel.getPermissionLeave())) {
                        // FIXME
                        channel.removeListener(sender.getName());
                        synchronized (WorldChannels.currentChannel) {
                            if(WorldChannels.currentChannel.get(
                                    sender.getName()).equals(channel)) {
                                WorldChannels.currentChannel.remove(sender
                                        .getName());
                            }
                        }
                        sender.sendMessage(ChatColor.GREEN + WorldChannels.TAG
                                + " Left channel '" + channel.getName() + "'");
                    } else {
                        info.put(Flag.EXTRA, channel.getPermissionLeave());
                        sender.sendMessage(Localizer.parseString(
                                LocalString.PERMISSION_DENY, info));
                    }
                } else {
                    info.put(Flag.EXTRA, args[1]);
                    info.put(Flag.REASON, "channel");
                    sender.sendMessage(Localizer.parseString(
                            LocalString.UNKNOWN, info));
                }

            } catch(ArrayIndexOutOfBoundsException e) {
                info.put(Flag.EXTRA, "channel");
                sender.sendMessage(Localizer.parseString(
                        LocalString.MISSING_PARAM, info));
            }
        }
        return true;
    }

}
