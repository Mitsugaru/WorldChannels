package com.mitsugaru.worldchannels.commands;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.config.ConfigHandler;
import com.mitsugaru.worldchannels.config.localize.Flag;
import com.mitsugaru.worldchannels.config.localize.LocalString;
import com.mitsugaru.worldchannels.config.localize.Localizer;

public class PrevCommand extends AbstractListCommand {

    @Override
    public boolean execute(WorldChannels plugin, CommandSender sender,
            Command command, String label, String[] args) {
        final Map<Flag, String> info = new EnumMap<Flag, String>(Flag.class);
        info.put(Flag.TAG, WorldChannels.TAG);
        if(!(sender instanceof Player)) {
            sender.sendMessage(Localizer.parseString(
                    LocalString.NO_CONSOLE, info));
        } else {
            listChannels(sender, pageWorld.get(sender.getName()), 0,
                    info, plugin.getModuleForClass(ConfigHandler.class));
        }
        return true;
    }

}
