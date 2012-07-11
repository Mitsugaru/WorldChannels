package com.mitsugaru.WorldChannels.permissions;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.mitsugaru.WorldChannels.WorldChannels;

/**
 * Class to handle permission node checks.
 * Mostly only to support PEX natively, due to
 * SuperPerm compatibility with PEX issues.
 *
 * @author Mitsugaru
 *
 */
public class PermissionHandler {
	private static Permission perm;
	private static boolean hasVault;
	
	public static void init(WorldChannels plugin)
	{
		if(plugin.getServer().getPluginManager().getPlugin("Vault") != null)
		{
			hasVault = true;
			RegisteredServiceProvider<Permission> permissionProvider = plugin
				.getServer()
				.getServicesManager()
				.getRegistration(Permission.class);
			if (permissionProvider != null)
			{
				perm = permissionProvider.getProvider();
			}
		}
		else
		{
			hasVault = false;
		}

	}
	
	public static boolean checkPermission(CommandSender sender, PermissionNode node)
	{
		return checkPermission(sender, node.getNode());
	}

	/**
	 *
	 * @param CommandSender that sent command
	 * @param PermissionNode node to check, as String
	 * @return true if sender has the node, else false
	 */
	public static boolean checkPermission(CommandSender sender, String node)
	{
		//Use vault if we have it
		if(hasVault && perm != null)
		{
			return perm.has(sender, node);
		}
		//Attempt to use SuperPerms or op
		if(sender.isOp() || sender.hasPermission(node))
		{
			return true;
		}
		//Else, they don't have permission
		return false;
	}
}
