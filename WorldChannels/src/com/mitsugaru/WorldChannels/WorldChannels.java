package com.mitsugaru.WorldChannels;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mitsugaru.WorldChannels.config.ConfigHandler;
import com.mitsugaru.WorldChannels.events.ChatListener;
import com.mitsugaru.WorldChannels.permissions.PermCheck;

public class WorldChannels extends JavaPlugin
{
	public static Chat chat;
	public static String TAG = "[WorldChannels]";
	private ConfigHandler configHandler;
	private PermCheck perm;

	/**
	 * Method that is called when plugin is enabled
	 */
	@Override
	public void onEnable()
	{
		//Initialize configs
		configHandler = new ConfigHandler(this);
		RegisteredServiceProvider<Chat> chatProvider = this.getServer()
				.getServicesManager().getRegistration(Chat.class);
		if (chatProvider != null)
		{
			chat = chatProvider.getProvider();
		}
		else
		{
			//They don't have vault (or an outdated version)
			//TODO disable
			this.getLogger().warning("Vault's Chat class not found! Disabling...");
		}
		// Setup permissions
		perm = new PermCheck(this);
		// Setup listeners
		final PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new ChatListener(this), this);
	}
	
	public ConfigHandler getConfigHandler()
	{
		return configHandler;
	}
	
	public PermCheck getPermissionsHandler()
	{
		return perm;
	}
}
