package com.mitsugaru.WorldChannels.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mitsugaru.WorldChannels.WorldChannels;

public class Config
{
	private String worldName;
	private static WorldChannels plugin;
	private static File file;
	private static YamlConfiguration config;

	public Config(WorldChannels wc, String worldName)
	{
		plugin = wc;
		this.worldName = worldName;
		// Grab file
		file = new File(plugin.getDataFolder().getAbsolutePath() + "/worlds/"
				+ worldName + ".yml");
		config = YamlConfiguration.loadConfiguration(file);
		loadDefaults();
		loadVariables();
	}

	public static void save()
	{
		// Set config
		try
		{
			// Save the file
			config.save(file);
		}
		catch (IOException e1)
		{
			plugin.getLogger().warning(
					"File I/O Exception on saving heroes config");
			e1.printStackTrace();
		}
	}

	public static void reload()
	{
		try
		{
			config.load(file);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
		loadDefaults();
		loadVariables();
	}

	private static void loadDefaults()
	{
		// LinkedHashmap of defaults
		final Map<String, Object> defaults = new LinkedHashMap<String, Object>();
		// TODO defaults
		defaults.put("formatter.use", false);
		defaults.put("formatter.format", "");
		// Add to config if missing
		for (final Entry<String, Object> e : defaults.entrySet())
		{
			if (!config.contains(e.getKey()))
			{
				config.set(e.getKey(), e.getValue());
			}
		}
		save();
	}

	private static void loadVariables()
	{
		// load variables
	}
	
	public String getWorldName()
	{
		return worldName;
	}
}
