package com.mitsugaru.worldchannels.commands;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.chat.Field;
import com.mitsugaru.worldchannels.chat.WChat;
import com.mitsugaru.worldchannels.config.ConfigHandler;
import com.mitsugaru.worldchannels.config.localize.Flag;
import com.mitsugaru.worldchannels.config.localize.LocalString;
import com.mitsugaru.worldchannels.config.localize.Localizer;
import com.mitsugaru.worldchannels.permissions.PermissionNode;
import com.mitsugaru.worldchannels.services.ICommand;

public class ShoutCommand implements ICommand {

    @Override
    public boolean execute(WorldChannels plugin, CommandSender sender,
            Command command, String label, String[] args) {
        final Map<Flag, String> info = new EnumMap<Flag, String>(Flag.class);
        info.put(Flag.TAG, WorldChannels.TAG);
        if(sender.hasPermission(PermissionNode.SHOUT.getNode())) {
            // Set info of fields for formatting message and format
            final EnumMap<Field, String> shoutInfo = new EnumMap<Field, String>(
                    Field.class);
            shoutInfo.put(Field.NAME, sender.getName());
            String worldName = "", groupName = "", prefix = "", suffix = "";
            if(sender instanceof Player) {
                worldName = ((Player) sender).getWorld().getName();
                try {
                    groupName = plugin.getChat().getPlayerGroups(
                            (Player) sender)[0];

                } catch(ArrayIndexOutOfBoundsException a) {
                    // IGNORE
                }
                prefix = plugin.getChat().getPlayerPrefix(worldName,
                        sender.getName());

                suffix = plugin.getChat().getPlayerSuffix(worldName,
                        sender.getName());
            }
            shoutInfo.put(Field.WORLD, worldName);
            shoutInfo.put(Field.GROUP, groupName);
            shoutInfo.put(Field.PREFIX, prefix);
            shoutInfo.put(Field.SUFFIX, suffix);
            final StringBuilder sb = new StringBuilder();
            String out = "";
            for(int i = 0; i < args.length; i++) {
                sb.append(args[i] + " ");
                out = sb.toString();
            }
            if(sb.length() > 0) {
                out = sb.toString().replaceAll("\\s+$", "");
            }
            shoutInfo.put(Field.MESSAGE, out);
            plugin.getServer().broadcastMessage(
                    WChat.parseString(
                            plugin.getModuleForClass(ConfigHandler.class)
                                    .getShoutFormat(), shoutInfo));
        } else {
            info.put(Flag.EXTRA, PermissionNode.SHOUT.getNode());
            sender.sendMessage(Localizer.parseString(
                    LocalString.PERMISSION_DENY, info));
        }
        return true;
    }
}
