package com.mitsugaru.WorldChannels;

import java.util.HashSet;
import java.util.Set;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mitsugaru.WorldChannels.config.ConfigHandler;
import com.mitsugaru.WorldChannels.config.LocalizeConfig;
import com.mitsugaru.WorldChannels.events.WChatListener;
import com.mitsugaru.WorldChannels.permissions.PermCheck;

//TODO friends list, mutual acception
//TODO join specific worlds
//TODO global chat, separate channel
//TODO world specific auto-announcer

public class WorldChannels extends JavaPlugin
{
	public static Chat chat;
	public static String TAG = "[WorldChannels]";
	private ConfigHandler configHandler;
	private PermCheck perm;
	public static Set<String> observers = new HashSet<String>();

	/**
	 * Method that is called when plugin is enabled
	 */
	@Override
	public void onEnable()
	{
		// Initialize configs
		configHandler = new ConfigHandler(this);
		LocalizeConfig.init(this);
		// Grab Chat class from Vault
		RegisteredServiceProvider<net.milkbowl.vault.chat.Chat> chatProvider = this.getServer()
				.getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null)
		{
			chat = chatProvider.getProvider();
			// Setup permissions
			perm = new PermCheck(this);
			// Setup commander
			getCommand("wc").setExecutor(new Commander(this));
			// Setup listeners
			final PluginManager pm = this.getServer().getPluginManager();
			pm.registerEvents(new WChatListener(this), this);
		}
		else
		{
			// They don't have vault (or have an outdated version)
			this.getLogger().warning(
					"Vault's Chat class not found! Disabling...");
			this.getServer().getPluginManager().disablePlugin(this);
		}
	}

	public ConfigHandler getConfigHandler()
	{
		return configHandler;
	}

	public PermCheck getPermissionsHandler()
	{
		return perm;
	}

	/**
	 * Thanks to Njol for the following method
	 * http://forums.bukkit.org/threads/multiple-classes-config-colours.79719/#post-1154761
	 * 
	 * @author Njol
	 */
	public static String colorizeText(String string)
	{
		return ChatColor.translateAlternateColorCodes('&', string);
	}
}
