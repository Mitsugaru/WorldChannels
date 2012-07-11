package com.mitsugaru.WorldChannels;

import java.util.EnumMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitsugaru.WorldChannels.WChat.Field;
import com.mitsugaru.WorldChannels.config.ConfigHandler;
import com.mitsugaru.WorldChannels.permissions.PermissionHandler;
import com.mitsugaru.WorldChannels.permissions.PermissionNode;

public class Commander implements CommandExecutor {
    private final WorldChannels plugin;
    private final ConfigHandler configHandler;
    private final static String bar = "======================";
    private long time;

    public Commander(WorldChannels plugin) {
	this.plugin = plugin;
	this.configHandler = plugin.getConfigHandler();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd,
	    String commandLabel, String[] args) {
	if (configHandler.debugTime) {
	    time = System.nanoTime();
	}
	// See if any arguments were given
	if (args.length == 0) {
	    // Check if they have "karma" permission
	    this.displayHelp(sender);
	} else {
	    final EnumMap<LocalString.Flag, String> info = new EnumMap<LocalString.Flag, String>(
		    LocalString.Flag.class);
	    info.put(LocalString.Flag.TAG, WorldChannels.TAG);
	    final String com = args[0].toLowerCase();
	    if (com.equals("version") || com.equals("ver")) {
		// Version and author
		this.showVersion(sender, args);
	    } else if (com.equals("?") || com.equals("help")) {
		this.displayHelp(sender);
	    } else if (com.equals("reload")) {
		if (PermissionHandler.checkPermission(sender, PermissionNode.ADMIN)) {
		    configHandler.reloadConfigs();
		    sender.sendMessage(LocalString.RELOAD_CONFIG
			    .parseString(info));
		} else {
		    info.put(LocalString.Flag.EXTRA,
			    PermissionNode.ADMIN.getNode());
		    sender.sendMessage(LocalString.PERMISSION_DENY
			    .parseString(info));
		}
	    } else if (com.equals("shout")) {
		if (PermissionHandler.checkPermission(sender, PermissionNode.SHOUT)) {
		    // Set info of fields for formatting message and format
		    final EnumMap<Field, String> shoutInfo = new EnumMap<Field, String>(
			    Field.class);
		    shoutInfo.put(Field.NAME, sender.getName());
		    String worldName = "", groupName = "", prefix = "", suffix = "";
		    if (sender instanceof Player) {
			worldName = ((Player) sender).getWorld().getName();
			try {
			    groupName = plugin.getChat()
				    .getPlayerGroups((Player) sender)[0];

			} catch (ArrayIndexOutOfBoundsException a) {
			    // IGNORE
			}
			prefix = plugin.getChat().getPlayerPrefix(
				worldName, sender.getName());

			suffix = plugin.getChat().getPlayerSuffix(
				worldName, sender.getName());
		    }
		    shoutInfo.put(Field.WORLD, worldName);
		    shoutInfo.put(Field.GROUP, groupName);
		    shoutInfo.put(Field.PREFIX, prefix);
		    shoutInfo.put(Field.SUFFIX, suffix);
		    final StringBuilder sb = new StringBuilder();
		    String out = "";
		    for (int i = 1; i < args.length; i++) {
			sb.append(args[i] + " ");
			out = sb.toString();
		    }
		    if (sb.length() > 0) {
			out = sb.toString().replaceAll("\\s+$", "");
		    }
		    shoutInfo.put(Field.MESSAGE, out);
		    plugin.getServer().broadcastMessage(
			    WChat.parseString(plugin.getConfigHandler()
				    .getShoutFormat(), shoutInfo));
		} else {
		    info.put(LocalString.Flag.EXTRA,
			    PermissionNode.SHOUT.getNode());
		    sender.sendMessage(LocalString.PERMISSION_DENY
			    .parseString(info));
		}
	    } else if (com.equals("observe") || com.equals("listen")) {
		if (PermissionHandler.checkPermission(sender, PermissionNode.OBSERVE)) {
		    final String name = sender.getName();
		    if (WorldChannels.observers.contains(name)) {
			// Remove from observer list
			WorldChannels.observers.remove(name);
			sender.sendMessage(LocalString.OBSERVER_OFF
				.parseString(info));
		    } else {
			// Add to observer list
			WorldChannels.observers.add(name);
			sender.sendMessage(LocalString.OBSERVER_ON
				.parseString(info));
		    }
		} else {
		    info.put(LocalString.Flag.EXTRA,
			    PermissionNode.OBSERVE.getNode());
		    sender.sendMessage(LocalString.PERMISSION_DENY
			    .parseString(info));
		}
	    } else {
		info.put(LocalString.Flag.EXTRA, com);
		sender.sendMessage(LocalString.UNKNOWN_COMMAND
			.parseString(info));
	    }
	}
	if (configHandler.debugTime) {
	    debugTime(sender, time);
	}
	return true;
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
	sender.sendMessage(LocalString.HELP_HELP.parseString(null));
	if (PermissionHandler.checkPermission(sender, PermissionNode.ADMIN)) {
	    sender.sendMessage(LocalString.HELP_ADMIN_RELOAD.parseString(null));
	}
	if (PermissionHandler.checkPermission(sender, PermissionNode.SHOUT)) {
	    sender.sendMessage(LocalString.HELP_SHOUT.parseString(null));
	}
	if(PermissionHandler.checkPermission(sender, PermissionNode.OBSERVE))
	{
	    sender.sendMessage(LocalString.HELP_OBSERVE.parseString(null));
	}
	sender.sendMessage(LocalString.HELP_VERSION.parseString(null));
    }

}
