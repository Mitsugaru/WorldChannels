package com.mitsugaru.WorldChannels.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mitsugaru.WorldChannels.LocalString;
import com.mitsugaru.WorldChannels.WorldChannels;
import com.mitsugaru.WorldChannels.permissions.PermissionHandler;
import com.mitsugaru.WorldChannels.permissions.PermissionNode;

public class WCPlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
	if (event.getPlayer() != null) {
	    final Player player = event.getPlayer();
	    if (PermissionHandler.checkPermission(player,
		    PermissionNode.OBSERVE_AUTO)) {
		WorldChannels.observers.add(player.getName());
		player.sendMessage(LocalString.OBSERVER_ON.parseString(null));
	    }
	}
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
	if (event.getPlayer() != null) {
	    WorldChannels.observers.remove(event.getPlayer().getName());
	}
    }
}
