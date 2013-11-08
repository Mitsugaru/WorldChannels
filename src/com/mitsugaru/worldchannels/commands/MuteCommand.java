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

public class MuteCommand extends AbstractChannelCommand {

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
                final String playerName = expandName(args[1]);
                if(playerName != null) {
                    final Player target = plugin.getServer().getPlayer(
                            playerName);
                    if(target != null) {
                        if(channel != null) {
                            if(sender
                                    .hasPermission(channel.getPermissionMute())) {
                                channel.addMutedPlayer(target.getName());
                                target.sendMessage(ChatColor.RED
                                        + WorldChannels.TAG
                                        + " You have been muted in channel '"
                                        + channel.getName() + "' by "
                                        + sender.getName());
                                sender.sendMessage(ChatColor.GREEN
                                        + WorldChannels.TAG + " Muted "
                                        + target.getName() + " in channel "
                                        + channel.getName());
                            } else {
                                info.put(Flag.EXTRA,
                                        channel.getPermissionMute());
                                sender.sendMessage(Localizer.parseString(
                                        LocalString.PERMISSION_DENY, info));
                            }
                        } else {
                            info.put(Flag.EXTRA, args[0]);
                            info.put(Flag.REASON, "channel");
                            sender.sendMessage(Localizer.parseString(
                                    LocalString.UNKNOWN, info));
                        }
                    } else {
                        info.put(Flag.EXTRA, args[1]);
                        info.put(Flag.REASON, "player");
                        sender.sendMessage(Localizer.parseString(
                                LocalString.UNKNOWN, info));
                    }
                } else {
                    info.put(Flag.EXTRA, args[1]);
                    info.put(Flag.REASON, "player");
                    sender.sendMessage(Localizer.parseString(
                            LocalString.UNKNOWN, info));
                }

            } catch(ArrayIndexOutOfBoundsException e) {
                info.put(Flag.EXTRA, "channel name");
                sender.sendMessage(Localizer.parseString(
                        LocalString.MISSING_PARAM, info));
            }
        }
        return true;
    }

}
