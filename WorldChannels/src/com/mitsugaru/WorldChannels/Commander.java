package com.mitsugaru.WorldChannels;

import java.util.EnumMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.mitsugaru.WorldChannels.config.ConfigHandler;
import com.mitsugaru.WorldChannels.permissions.PermCheck;
import com.mitsugaru.WorldChannels.permissions.Permission;

public class Commander implements CommandExecutor
{
	private final WorldChannels plugin;
	private final PermCheck perm;
	private final ConfigHandler configHandler;
	private final static String bar = "======================";
	private long time;

	public Commander(WorldChannels plugin)
	{
		this.plugin = plugin;
		this.configHandler = plugin.getConfigHandler();
		this.perm = plugin.getPermissionsHandler();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args)
	{
		if (configHandler.debugTime)
		{
			time = System.nanoTime();
		}
		// See if any arguments were given
		if (args.length == 0)
		{
			// Check if they have "karma" permission
			this.displayHelp(sender);
		}
		else
		{
			final EnumMap<LocalString.Flag, String> info = new EnumMap<LocalString.Flag, String>(
					LocalString.Flag.class);
			info.put(LocalString.Flag.TAG, WorldChannels.TAG);
			final String com = args[0].toLowerCase();
			if (com.equals("version") || com.equals("ver"))
			{
				// Version and author
				this.showVersion(sender, args);
			}
			else if (com.equals("?") || com.equals("help"))
			{
				this.displayHelp(sender);
			}
			else if (com.equals("reload"))
			{
				if (perm.checkPermission(sender, Permission.ADMIN.getNode()))
				{
					configHandler.reloadConfigs();
					sender.sendMessage(LocalString.RELOAD_CONFIG.parseString(info));
				}
				else
				{
					info.put(LocalString.Flag.EXTRA, Permission.ADMIN.getNode());
					sender.sendMessage(LocalString.PERMISSION_DENY.parseString(info));
				}
			}
			else
			{
				info.put(LocalString.Flag.EXTRA, com);
				sender.sendMessage(LocalString.UNKNOWN_COMMAND.parseString(info));
			}
		}
		if (configHandler.debugTime)
		{
			debugTime(sender, time);
		}
		return true;
	}

	private void debugTime(CommandSender sender, long time)
	{
		time = System.nanoTime() - time;
		sender.sendMessage("[Debug]" + WorldChannels.TAG + "Process time: "
				+ time);
	}

	private void showVersion(CommandSender sender, String[] args)
	{
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
	private void displayHelp(CommandSender sender)
	{
		sender.sendMessage(ChatColor.BLUE + "==========" + ChatColor.GOLD
				+ "WorldChannels" + ChatColor.BLUE + "==========");
		sender.sendMessage(LocalString.HELP_HELP.parseString(null));
		if (perm.checkPermission(sender, Permission.ADMIN.getNode()))
		{
			sender.sendMessage(LocalString.HELP_ADMIN_RELOAD.parseString(null));
		}
		sender.sendMessage(LocalString.HELP_VERSION.parseString(null));
	}

}
