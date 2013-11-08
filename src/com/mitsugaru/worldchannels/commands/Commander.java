package com.mitsugaru.worldchannels.commands;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.chat.Channel;
import com.mitsugaru.worldchannels.chat.WChat;
import com.mitsugaru.worldchannels.chat.Field;
import com.mitsugaru.worldchannels.config.ConfigHandler;
import com.mitsugaru.worldchannels.config.WorldConfig;
import com.mitsugaru.worldchannels.config.localize.Flag;
import com.mitsugaru.worldchannels.config.localize.LocalString;
import com.mitsugaru.worldchannels.config.localize.Localizer;
import com.mitsugaru.worldchannels.permissions.PermissionNode;

public class Commander implements CommandExecutor {
    private final WorldChannels plugin;
    private final ConfigHandler configHandler;
    private final static String bar = "======================";
    private final static int LIST_LIMIT = 8;
    public static final Map<String, Integer> page = new HashMap<String, Integer>();
    public static final Map<String, String> pageWorld = new HashMap<String, String>();
    private long time;

    public Commander(WorldChannels plugin) {
        this.plugin = plugin;
        this.configHandler = plugin.getModuleForClass(ConfigHandler.class);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
            String commandLabel, String[] args) {
        if(configHandler.debugTime) {
            time = System.nanoTime();
        }
        // See if any arguments were given
        if(args.length == 0) {
            // Check if they have "karma" permission
            this.displayHelp(sender);
        } else {
            final EnumMap<Flag, String> info = new EnumMap<Flag, String>(
                    Flag.class);
            info.put(Flag.TAG, WorldChannels.TAG);
            final String com = args[0].toLowerCase();
            if(com.equals("version") || com.equals("ver")) {
                // Version and author
                this.showVersion(sender, args);
            } else if(com.equals("?") || com.equals("help")) {
                this.displayHelp(sender);
            } else if(com.equals("reload")) {
                if(sender.hasPermission(PermissionNode.ADMIN.getNode())) {
                    configHandler.reloadConfigs();
                    sender.sendMessage(Localizer.parseString(
                            LocalString.RELOAD_CONFIG, info));
                } else {
                    info.put(Flag.EXTRA, PermissionNode.ADMIN.getNode());
                    sender.sendMessage(Localizer.parseString(
                            LocalString.PERMISSION_DENY, info));
                }
            } else if(com.equals("shout")) {
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
                    for(int i = 1; i < args.length; i++) {
                        sb.append(args[i] + " ");
                        out = sb.toString();
                    }
                    if(sb.length() > 0) {
                        out = sb.toString().replaceAll("\\s+$", "");
                    }
                    shoutInfo.put(Field.MESSAGE, out);
                    plugin.getServer().broadcastMessage(
                            WChat.parseString(configHandler.getShoutFormat(),
                                    shoutInfo));
                } else {
                    info.put(Flag.EXTRA, PermissionNode.SHOUT.getNode());
                    sender.sendMessage(Localizer.parseString(
                            LocalString.PERMISSION_DENY, info));
                }
            } else if(com.equals("observe") || com.equals("listen")) {
                if(sender.hasPermission(PermissionNode.OBSERVE.getNode())) {
                    if(!(sender instanceof Player)) {
                        sender.sendMessage(Localizer.parseString(
                                LocalString.NO_CONSOLE, info));
                    }
                    if(args.length < 2) {
                        // did not specify a channel
                        info.put(Flag.EXTRA, "channel");
                        sender.sendMessage(Localizer.parseString(
                                LocalString.MISSING_PARAM, info));
                    } else {
                        final String wc = args[1].toLowerCase();
                        if(wc.contains(":")) {
                            final String[] split = wc.split(":");
                            // parse for world
                            final String worldName = split[0];
                            final String channelName = split[1];
                            final WorldConfig conf = configHandler
                                    .getWorldConfig(worldName);
                            final Channel channel = conf
                                    .getChannel(channelName);
                            if(channel != null) {
                                if(channel.getObservers().contains(
                                        sender.getName())) {
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
                            boolean found = false;
                            final String worldName = ((Player) sender)
                                    .getWorld().getName();
                            final WorldConfig conf = configHandler
                                    .getWorldConfig(worldName);
                            Channel channel = conf.getChannel(wc);
                            if(channel != null) {
                                found = true;
                                if(channel.getObservers().contains(
                                        sender.getName())) {
                                    channel.removeObserver(sender.getName());
                                    sender.sendMessage(Localizer.parseString(
                                            LocalString.OBSERVER_OFF, info));
                                } else {
                                    channel.addObserver(sender.getName());
                                    sender.sendMessage(Localizer.parseString(
                                            LocalString.OBSERVER_ON, info));
                                }
                            }
                            if(!found) {
                                // check global channels
                                for(Channel globalChannel : configHandler
                                        .getGlobalChannels()) {
                                    if(globalChannel.getName()
                                            .equalsIgnoreCase(wc)) {
                                        if(globalChannel.getObservers()
                                                .contains(sender.getName())) {
                                            globalChannel.removeObserver(sender
                                                    .getName());
                                            sender.sendMessage(Localizer
                                                    .parseString(
                                                            LocalString.OBSERVER_OFF,
                                                            info));
                                            break;
                                        } else {
                                            globalChannel.addObserver(sender
                                                    .getName());
                                            sender.sendMessage(Localizer
                                                    .parseString(
                                                            LocalString.OBSERVER_ON,
                                                            info));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    info.put(Flag.EXTRA, PermissionNode.OBSERVE.getNode());
                    sender.sendMessage(Localizer.parseString(
                            LocalString.PERMISSION_DENY, info));
                }
            } else if(com.equals("list")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(Localizer.parseString(
                            LocalString.NO_CONSOLE, info));
                } else {
                    String world = ((Player) sender).getWorld().getName();
                    try {
                        world = args[1];
                    } catch(ArrayIndexOutOfBoundsException e) {
                        // Ignore
                    }
                    pageWorld.put(sender.getName(), world);
                    listChannels(sender, world, 0, info);
                }
            } else if(com.equals("next")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(Localizer.parseString(
                            LocalString.NO_CONSOLE, info));
                } else {
                    listChannels(sender, pageWorld.get(sender.getName()), 0,
                            info);
                }
            } else if(com.equals("prev")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(Localizer.parseString(
                            LocalString.NO_CONSOLE, info));
                } else {
                    listChannels(sender, pageWorld.get(sender.getName()), 0,
                            info);
                }
            } else if(com.equals("join")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(Localizer.parseString(
                            LocalString.NO_CONSOLE, info));
                } else {
                    try {
                        final Channel channel = parseChannel(sender, args[1]);
                        if(channel != null) {
                            if(sender
                                    .hasPermission(channel.getPermissionJoin())) {
                                // FIXME
                                channel.addListener(sender.getName());
                                synchronized (WorldChannels.currentChannel) {
                                    WorldChannels.currentChannel.put(
                                            sender.getName(), channel);
                                }
                                sender.sendMessage(ChatColor.GREEN
                                        + WorldChannels.TAG
                                        + " Joined channel '"
                                        + channel.getName() + "'");
                            } else {
                                info.put(Flag.EXTRA,
                                        channel.getPermissionJoin());
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
            } else if(com.equals("leave")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(Localizer.parseString(
                            LocalString.NO_CONSOLE, info));
                } else {
                    try {
                        final Channel channel = parseChannel(sender, args[1]);
                        if(channel != null) {
                            if(sender.hasPermission(channel
                                    .getPermissionLeave())) {
                                // FIXME
                                channel.removeListener(sender.getName());
                                synchronized (WorldChannels.currentChannel) {
                                    if(WorldChannels.currentChannel.get(
                                            sender.getName()).equals(channel)) {
                                        WorldChannels.currentChannel
                                                .remove(sender.getName());
                                    }
                                }
                                sender.sendMessage(ChatColor.GREEN
                                        + WorldChannels.TAG + " Left channel '"
                                        + channel.getName() + "'");
                            } else {
                                info.put(Flag.EXTRA,
                                        channel.getPermissionLeave());
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
            } else if(com.equals("kick")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(Localizer.parseString(
                            LocalString.NO_CONSOLE, info));
                } else {
                    try {
                        final Channel channel = parseChannel(sender, args[1]);
                        final String playerName = expandName(args[2]);
                        if(playerName != null) {
                            final Player target = plugin.getServer().getPlayer(
                                    playerName);
                            if(target != null) {
                                if(channel != null) {
                                    if(sender.hasPermission(channel
                                            .getPermissionKick())) {
                                        channel.removeListener(target.getName());
                                        synchronized (WorldChannels.currentChannel) {
                                            if(WorldChannels.currentChannel
                                                    .get(target.getName())
                                                    .equals(channel)) {
                                                WorldChannels.currentChannel
                                                        .remove(target
                                                                .getName());
                                            }
                                        }
                                        target.sendMessage(ChatColor.RED
                                                + WorldChannels.TAG
                                                + " You have been kicked from channel '"
                                                + channel.getName() + "' by "
                                                + sender.getName());
                                        sender.sendMessage(ChatColor.GREEN
                                                + WorldChannels.TAG
                                                + " Kicked " + target.getName()
                                                + " from channel "
                                                + channel.getName());
                                    } else {
                                        info.put(Flag.EXTRA,
                                                channel.getPermissionKick());
                                        sender.sendMessage(Localizer
                                                .parseString(
                                                        LocalString.PERMISSION_DENY,
                                                        info));
                                    }
                                } else {
                                    info.put(Flag.EXTRA, args[1]);
                                    info.put(Flag.REASON, "channel");
                                    sender.sendMessage(Localizer.parseString(
                                            LocalString.UNKNOWN, info));
                                }
                            } else {
                                info.put(Flag.EXTRA, args[2]);
                                info.put(Flag.REASON, "player");
                                sender.sendMessage(Localizer.parseString(
                                        LocalString.UNKNOWN, info));
                            }
                        } else {
                            info.put(Flag.EXTRA, args[2]);
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
            } else if(com.equals("mute")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(Localizer.parseString(
                            LocalString.NO_CONSOLE, info));
                } else {
                    try {
                        final Channel channel = parseChannel(sender, args[1]);
                        final String playerName = expandName(args[2]);
                        if(playerName != null) {
                            final Player target = plugin.getServer().getPlayer(
                                    playerName);
                            if(target != null) {
                                if(channel != null) {
                                    if(sender.hasPermission(channel
                                            .getPermissionMute())) {
                                        channel.addMutedPlayer(target.getName());
                                        target.sendMessage(ChatColor.RED
                                                + WorldChannels.TAG
                                                + " You have been muted in channel '"
                                                + channel.getName() + "' by "
                                                + sender.getName());
                                        sender.sendMessage(ChatColor.GREEN
                                                + WorldChannels.TAG + " Muted "
                                                + target.getName()
                                                + " in channel "
                                                + channel.getName());
                                    } else {
                                        info.put(Flag.EXTRA,
                                                channel.getPermissionMute());
                                        sender.sendMessage(Localizer
                                                .parseString(
                                                        LocalString.PERMISSION_DENY,
                                                        info));
                                    }
                                } else {
                                    info.put(Flag.EXTRA, args[1]);
                                    info.put(Flag.REASON, "channel");
                                    sender.sendMessage(Localizer.parseString(
                                            LocalString.UNKNOWN, info));
                                }
                            } else {
                                info.put(Flag.EXTRA, args[2]);
                                info.put(Flag.REASON, "player");
                                sender.sendMessage(Localizer.parseString(
                                        LocalString.UNKNOWN, info));
                            }
                        } else {
                            info.put(Flag.EXTRA, args[2]);
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
            } else if(com.equals("unmute")) {
                if(!(sender instanceof Player)) {
                    sender.sendMessage(Localizer.parseString(
                            LocalString.NO_CONSOLE, info));
                } else {
                    try {
                        final Channel channel = parseChannel(sender, args[1]);
                        final String playerName = expandName(args[2]);
                        if(playerName != null) {
                            final Player target = plugin.getServer().getPlayer(
                                    playerName);
                            if(target != null) {
                                if(channel != null) {
                                    if(sender.hasPermission(channel
                                            .getPermissionMute())) {
                                        channel.removeMutedPlayer(target
                                                .getName());
                                        target.sendMessage(ChatColor.YELLOW
                                                + WorldChannels.TAG
                                                + " You have been unmuted in channel '"
                                                + channel.getName() + "' by "
                                                + sender.getName());
                                        sender.sendMessage(ChatColor.GREEN
                                                + WorldChannels.TAG
                                                + " Unmuted "
                                                + target.getName()
                                                + " in channel "
                                                + channel.getName());
                                    } else {
                                        info.put(Flag.EXTRA,
                                                channel.getPermissionMute());
                                        sender.sendMessage(Localizer
                                                .parseString(
                                                        LocalString.PERMISSION_DENY,
                                                        info));
                                    }
                                } else {
                                    info.put(Flag.EXTRA, args[1]);
                                    info.put(Flag.REASON, "channel");
                                    sender.sendMessage(Localizer.parseString(
                                            LocalString.UNKNOWN, info));
                                }
                            } else {
                                info.put(Flag.EXTRA, args[2]);
                                info.put(Flag.REASON, "player");
                                sender.sendMessage(Localizer.parseString(
                                        LocalString.UNKNOWN, info));
                            }
                        } else {
                            info.put(Flag.EXTRA, args[2]);
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
            } else {
                info.put(Flag.EXTRA, com);
                info.put(Flag.REASON, "command");
                sender.sendMessage(Localizer.parseString(LocalString.UNKNOWN,
                        info));
            }
        }
        if(configHandler.debugTime) {
            debugTime(sender, time);
        }
        return true;
    }

    private Channel parseChannel(CommandSender sender, String param) {
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

    private void debugTime(CommandSender sender, long time) {
        time = System.nanoTime() - time;
        sender.sendMessage("[Debug]" + WorldChannels.TAG + "Process time: "
                + time);
    }

    private void showVersion(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.BLUE + bar + "=====");
        sender.sendMessage(ChatColor.GREEN + "WorldChannels v"
                + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GREEN + "Coded by Mitsugaru");
        sender.sendMessage(ChatColor.BLUE + "===========" + ChatColor.GRAY
                + "Config" + ChatColor.BLUE + "===========");
    }

    /**
     * Show the help menu, with commands and description
     * 
     * @param sender
     *            to display to
     */
    private void displayHelp(CommandSender sender) {
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
    }

    private void listChannels(CommandSender sender, String world,
            int pageAdjust, EnumMap<Flag, String> info) {
        if(!page.containsKey(sender.getName())) {
            page.put(sender.getName(), 0);
        } else if(pageAdjust != 0) {
            page.put(sender.getName(), page.get(sender.getName()) + pageAdjust);
        }
        List<Channel> hold = new ArrayList<Channel>();
        hold.addAll(configHandler.getGlobalChannels());
        try {
            hold.addAll(configHandler.getWorldConfig(world).getChannels());
        } catch(IllegalArgumentException e) {
            // Ignore
            info.put(Flag.EXTRA, world);
            info.put(Flag.REASON, "world");
            sender.sendMessage(Localizer.parseString(LocalString.UNKNOWN, info));
        }
        if(hold.isEmpty()) {
            // notify player that there are no available channels... somehow.
            sender.sendMessage(ChatColor.YELLOW + WorldChannels.TAG
                    + " No channels...");
            return;
        }
        Channel[] list = hold.toArray(new Channel[0]);
        for(int i = (page.get(sender.getName()).intValue() * LIST_LIMIT); i < (page
                .get(sender.getName()).intValue() * LIST_LIMIT + LIST_LIMIT); i++) {
            if(i < list.length) {
                if(list[i].getListeners().contains(sender.getName())) {
                    // They are a listener
                    sender.sendMessage(ChatColor.GREEN + "#" + list[i].getTag()
                            + " | " + list[i].getName());
                } else if(list[i].getObservers().contains(sender.getName())) {
                    // They are an observer
                    sender.sendMessage(ChatColor.GOLD + "#" + list[i].getTag()
                            + " | " + list[i].getName());
                } else if(list[i].getMuted().contains(sender.getName())) {
                    // They are muted
                    sender.sendMessage(ChatColor.RED + "#" + list[i].getTag()
                            + " | " + list[i].getName());
                } else {
                    // They are not a part of the channel
                    sender.sendMessage(ChatColor.GRAY + "#" + list[i].getTag()
                            + " | " + list[i].getName());
                }
            } else {
                break;
            }
        }
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
        for(int n = 0; n < plugin.getServer().getOnlinePlayers().length; n++) {
            String str = plugin.getServer().getOnlinePlayers()[n].getName();
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