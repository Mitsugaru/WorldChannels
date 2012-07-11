package com.mitsugaru.WorldChannels.events;

import org.bukkit.event.Listener;

import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;

import com.mitsugaru.WorldChannels.WorldChannels;

public class McMMOListener implements Listener {
    
    private WorldChannels plugin;
    
    public McMMOListener(WorldChannels plugin)
    {
	this.plugin = plugin;
    }

    public void McMMOAdminChatEvent(McMMOAdminChatEvent event)
    {
	if(event.getSender() != null)
	{
	    plugin.getLogger().info("admin chat event");
	    WorldChannels.mcmmoChat.add(event.getSender());
	}
    }
    
    public void McMMOPartyChatEvent(McMMOPartyChatEvent event)
    {
	if(event.getSender() != null)
	{
	    plugin.getLogger().info("party chat event");
	    WorldChannels.mcmmoChat.add(event.getSender());
	}
    }
}
