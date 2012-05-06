package com.mitsugaru.WorldChannels.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import com.mitsugaru.WorldChannels.WorldChannels;

public class ChatListener implements Listener
{
	private WorldChannels plugin;

	public ChatListener(WorldChannels plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void chatEvent(final PlayerChatEvent event)
	{
		plugin.getLogger().info("Format: " + event.getFormat());
		plugin.getLogger().info("Message: " + event.getMessage());
	}
}
