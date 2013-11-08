package com.mitsugaru.worldchannels.commands;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.chat.Channel;
import com.mitsugaru.worldchannels.chat.ChannelManager;
import com.mitsugaru.worldchannels.config.ConfigHandler;
import com.mitsugaru.worldchannels.config.WorldConfig;
import com.mitsugaru.worldchannels.config.localize.Flag;
import com.mitsugaru.worldchannels.config.localize.LocalString;
import com.mitsugaru.worldchannels.config.localize.Localizer;
import com.mitsugaru.worldchannels.permissions.PermissionNode;
import com.mitsugaru.worldchannels.services.ICommand;

public class ObserveCommand implements ICommand {

    @Override
    public boolean execute(WorldChannels plugin, CommandSender sender,
            Command command, String label, String[] args) {
        final Map<Flag, String> info = new EnumMap<Flag, String>(Flag.class);
        info.put(Flag.TAG, WorldChannels.TAG);
        if(sender.hasPermission(PermissionNode.OBSERVE.getNode())) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(Localizer.parseString(
                        LocalString.NO_CONSOLE, info));
            }
            if(args.length < 1) {
                // did not specify a channel, toggle global
                ChannelManager manager = plugin.getModuleForClass(ChannelManager.class);
                if(manager.getObservers().contains(sender.getName())) {
                    manager.getObservers().remove(sender.getName());
                    sender.sendMessage(Localizer.parseString(
                            LocalString.OBSERVER_OFF, info));
                } else {
                    manager.getObservers().add(sender.getName());
                    sender.sendMessage(Localizer.parseString(
                            LocalString.OBSERVER_ON, info));
                }
            } else {
                final String wc = args[0].toLowerCase();
                if(wc.contains(":")) {
                    final String[] split = wc.split(":");
                    // parse for world
                    final String worldName = split[0];
                    final String channelName = split[1];
                    final WorldConfig conf = plugin.getModuleForClass(
                            ConfigHandler.class).getWorldConfig(worldName);
                    final Channel channel = conf.getChannel(channelName);
                    if(channel != null) {
                        if(channel.getObservers().contains(sender.getName())) {
                            channel.removeObserver(sender.getName());
                            sender.sendMessage(Localizer.parseString(
                                    LocalString.OBSERVER_OFF, info));
                        } else {
                            channel.addObserver(sender.getName());
                            sender.sendMessage(Localizer.parseString(
                                    LocalString.OBSERVER_ON, info));
                        }
                    } else {
                        info.put(Flag.EXTRA, wc);
                        info.put(Flag.REASON, "channel");
                        sender.sendMessage(Localizer.parseString(
                                LocalString.UNKNOWN, info));
                    }
                } else {
                    // check local world
                    final String worldName = ((Player) sender).getWorld()
                            .getName();
                    final WorldConfig conf = plugin.getModuleForClass(
                            ConfigHandler.class).getWorldConfig(worldName);
                    Channel channel = conf.getChannel(wc);
                    if(channel != null) {
                        if(channel.getObservers().contains(sender.getName())) {
                            channel.removeObserver(sender.getName());
                            sender.sendMessage(Localizer.parseString(
                                    LocalString.OBSERVER_OFF, info));
                        } else {
                            channel.addObserver(sender.getName());
                            sender.sendMessage(Localizer.parseString(
                                    LocalString.OBSERVER_ON, info));
                        }
                    }
                }
            }
        } else {
            info.put(Flag.EXTRA, PermissionNode.OBSERVE.getNode());
            sender.sendMessage(Localizer.parseString(
                    LocalString.PERMISSION_DENY, info));
        }
        return true;
    }

}
