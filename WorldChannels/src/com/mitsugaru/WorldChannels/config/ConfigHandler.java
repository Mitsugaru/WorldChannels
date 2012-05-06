package com.mitsugaru.WorldChannels.config;

import java.util.HashMap;
import java.util.Map;

import com.mitsugaru.WorldChannels.WorldChannels;

public class ConfigHandler
{
	private WorldChannels plugin;
	private Map<String, Config> configs = new HashMap<String, Config>();
	
	public ConfigHandler(WorldChannels plugin)
	{
		this.plugin = plugin;
	}
}
