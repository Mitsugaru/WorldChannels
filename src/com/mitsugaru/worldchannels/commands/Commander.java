package com.mitsugaru.worldchannels.commands;

import java.util.EnumMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.config.ConfigHandler;
import com.mitsugaru.worldchannels.config.localize.Flag;
import com.mitsugaru.worldchannels.config.localize.LocalString;
import com.mitsugaru.worldchannels.config.localize.Localizer;
import com.mitsugaru.worldchannels.services.CommandHandler;

public class Commander extends CommandHandler {
    private final HelpCommand help = new HelpCommand();
    private long time;

    public Commander(WorldChannels plugin) {
        super(plugin, "wc");
        VersionCommand version = new VersionCommand();
        registerCommand("version", version);
        registerCommand("ver", version);
        registerCommand("?", help);
        registerCommand("help", help);
        registerCommand("reload", new ReloadCommand());
        registerCommand("shout", new ShoutCommand());
        ObserveCommand observe = new ObserveCommand();
        registerCommand("observe", observe);
        registerCommand("listen", observe);
        //registerCommand("list", new ListCommand());
        //registerCommand("next", new NextCommand());
        //registerCommand("prev", new PrevCommand());
        //registerCommand("join", new JoinCommand());
        //registerCommand("leave", new LeaveCommand());
        //registerCommand("kick", new KickCommand());
        registerCommand("mute", new MuteCommand());
        registerCommand("unmute", new UnmuteCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        if(plugin.getModuleForClass(ConfigHandler.class).debugTime) {
            time = System.nanoTime();
        }
        boolean value = super.onCommand(sender, command, label, args);
        if(plugin.getModuleForClass(ConfigHandler.class).debugTime) {
            debugTime(sender, time);
        }
        return value;
    }

    private void debugTime(CommandSender sender, long time) {
        time = System.nanoTime() - time;
        sender.sendMessage("[Debug]" + WorldChannels.TAG + "Process time: "
                + time);
    }

    @Override
    public boolean noArgs(CommandSender sender, Command command, String label) {
        return help.execute(plugin, sender, command, label, null);
    }

    @Override
    public boolean unknownCommand(CommandSender sender, Command command,
            String label, String[] args) {
        final Map<Flag, String> info = new EnumMap<Flag, String>(
                Flag.class);
        info.put(Flag.TAG, WorldChannels.TAG);
        info.put(Flag.EXTRA, args[1]);
        info.put(Flag.REASON, "command");
        sender.sendMessage(Localizer.parseString(LocalString.UNKNOWN,
                info));
        return true;
    }

}
