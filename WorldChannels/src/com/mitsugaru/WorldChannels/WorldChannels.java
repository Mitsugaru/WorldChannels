package com.mitsugaru.WorldChannels;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.mitsugaru.WorldChannels.events.ChatListener;
import com.mitsugaru.WorldChannels.events.PlayerListener;

public class WorldChannels extends JavaPlugin
{
	/**
	 * Method that is called when plugin is enabled
	 */
	@Override
	public void onEnable()
	{
		//Setup listeners
		final PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new PlayerListener(this), this);
		pm.registerEvents(new ChatListener(this), this);
	}
}
