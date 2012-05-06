package com.mitsugaru.WorldChannels.events;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import com.mitsugaru.WorldChannels.WChat;
import com.mitsugaru.WorldChannels.WChat.Field;
import com.mitsugaru.WorldChannels.WorldChannels;
import com.mitsugaru.WorldChannels.config.Config;
import com.mitsugaru.WorldChannels.config.ConfigHandler;

public class ChatListener implements Listener
{
	private WorldChannels plugin;
	private ConfigHandler configHandler;

	public ChatListener(WorldChannels plugin)
	{
		this.plugin = plugin;
		this.configHandler = plugin.getConfigHandler();
	}

	/**
	 * Listen for ChatEvents to change who listens to it. Set to HIGHEST
	 * priority so that other chat plugins can do what they need to do to the
	 * message/format.
	 * 
	 * @param PlayerChatEvent
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void chatEvent(final PlayerChatEvent event)
	{
		// Don't care about event if it is cancelled
		if (!event.isCancelled() && event.getPlayer() != null)
		{
			if (event.getPlayer().getWorld() != null)
			{
				// Get world name
				final String worldName = event.getPlayer().getWorld().getName();
				// Grab world specific config
				final Config config = plugin.getConfigHandler().getWorldConfig(
						worldName);
				Set<Player> receivers;
				if (config.includeLocalPlayers())
				{
					// Add people of the original world
					receivers = new HashSet<Player>(event.getPlayer()
							.getWorld().getPlayers());
				}
				else
				{
					receivers = new HashSet<Player>();
				}
				// Grab list
				final Set<String> worldList = plugin.getConfigHandler()
						.getWorldChannels(worldName);
				// Check if empty. If empty, we don't add any other world checks
				if (!worldList.isEmpty())
				{
					for (Player player : plugin.getServer().getOnlinePlayers())
					{
						try
						{
							if (worldList.contains(player.getWorld().getName()))
							{
								receivers.add(player);
							}
						}
						catch (NullPointerException n)
						{
							// IGNORE
						}
					}
				}
				// Clear recipients
				event.getRecipients().clear();
				// Add our receivers
				event.getRecipients().addAll(receivers);
				// Check if we are going to edit the format at all
				final EnumMap<Field, String> info = new EnumMap<Field, String>(
						Field.class);

				if (config.useFormatter())
				{
					final String format = config.getFormat();
					if (!format.equals(""))
					{
						event.setFormat(WChat.parseString(format, info));
					}
					// Edit this to change the format: world playername
					// prefix/suffix
					// etc
					plugin.getLogger().info("Format: " + event.getFormat());
				}
				else if(configHandler.useFormatter())
				{
					
				}
				// Edit this to change the message
				plugin.getLogger().info("Message: " + event.getMessage());
			}
		}
	}
}
