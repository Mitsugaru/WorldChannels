package com.mitsugaru.WorldChannels;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mitsugaru.WorldChannels.config.ConfigHandler;
import com.mitsugaru.WorldChannels.config.LocalizeConfig;
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
		// Initialize configs
		configHandler = new ConfigHandler(this);
		LocalizeConfig.init(this);
		// Grab Chat class from Vault
		RegisteredServiceProvider<Chat> chatProvider = this.getServer()
				.getServicesManager().getRegistration(Chat.class);
		if (chatProvider != null)
		{
			chat = chatProvider.getProvider();
			// Setup permissions
			perm = new PermCheck(this);
			// Setup commander
			getCommand("wc").setExecutor(new Commander(this));
			// Setup listeners
			final PluginManager pm = this.getServer().getPluginManager();
			pm.registerEvents(new ChatListener(this), this);
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
	 * Colorizes a given string to Bukkit standards
	 * 
	 * http://forums.bukkit.org/threads/methode-to-colorize.69543/#post-1063437
	 * 
	 * @param string
	 * @return String with appropriate Bukkit ChatColor in them
	 * @author AmberK
	 */
	public static String colorizeText(String string)
	{
		/**
		 * Colors
		 */
		string = string.replaceAll("&0", "" + ChatColor.BLACK);
		string = string.replaceAll("&1", "" + ChatColor.DARK_BLUE);
		string = string.replaceAll("&2", "" + ChatColor.DARK_GREEN);
		string = string.replaceAll("&3", "" + ChatColor.DARK_AQUA);
		string = string.replaceAll("&4", "" + ChatColor.DARK_RED);
		string = string.replaceAll("&5", "" + ChatColor.DARK_PURPLE);
		string = string.replaceAll("&6", "" + ChatColor.GOLD);
		string = string.replaceAll("&7", "" + ChatColor.GRAY);
		string = string.replaceAll("&8", "" + ChatColor.DARK_GRAY);
		string = string.replaceAll("&9", "" + ChatColor.BLUE);
		string = string.replaceAll("&a", "" + ChatColor.GREEN);
		string = string.replaceAll("&b", "" + ChatColor.AQUA);
		string = string.replaceAll("&c", "" + ChatColor.RED);
		string = string.replaceAll("&d", "" + ChatColor.LIGHT_PURPLE);
		string = string.replaceAll("&e", "" + ChatColor.YELLOW);
		string = string.replaceAll("&f", "" + ChatColor.WHITE);
		/**
		 * Formatting
		 */
		string = string.replaceAll("&k", "" + ChatColor.MAGIC);
		string = string.replaceAll("&l", "" + ChatColor.BOLD);
		string = string.replaceAll("&m", "" + ChatColor.STRIKETHROUGH);
		string = string.replaceAll("&n", "" + ChatColor.UNDERLINE);
		string = string.replaceAll("&o", "" + ChatColor.ITALIC);
		string = string.replaceAll("&r", "" + ChatColor.RESET);
		return string;
	}
}
