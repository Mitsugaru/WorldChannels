package com.mitsugaru.WorldChannels.config;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;

import com.mitsugaru.WorldChannels.WorldChannels;

public class ConfigHandler
{
	private WorldChannels plugin;
	private Map<String, Config> configs = new HashMap<String, Config>();
	
	public ConfigHandler(WorldChannels plugin)
	{
		this.plugin = plugin;
		//Check if worlds folder exists
		final File file = new File(plugin.getDataFolder().getAbsolutePath() + "/worlds");
		if(!file.exists())
		{
			//Create directory
			file.mkdir();
		}
		//Load config per world
		final List<World> worlds = plugin.getServer().getWorlds();
		for(World world : worlds)
		{
			final String worldName = world.getName();
			configs.put(worldName, new Config(plugin, worldName));
		}
		plugin.getLogger().info("Configuration loaded");
	}
}
