package com.mitsugaru.WorldChannels.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mitsugaru.WorldChannels.WorldChannels;
import com.mitsugaru.WorldChannels.config.WorldConfig;

public class WCPlayerListener implements Listener{
   private WorldChannels plugin;

   public WCPlayerListener(WorldChannels plugin){
      this.plugin = plugin;
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onPlayerJoin(PlayerJoinEvent event){
      // Send player to default channel
      try{
         final WorldConfig config = plugin.getConfigHandler().getWorldConfig(
               event.getPlayer().getWorld().getName());
         synchronized (WorldChannels.currentChannel){
            WorldChannels.currentChannel.put(event.getPlayer().getName(),
                  config.getDefaultChannel());
         }
      }catch(NullPointerException npe){
         // IGNORE
      }

   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onPlayerQuit(PlayerQuitEvent event){
      if(event.getPlayer() != null){
         synchronized (WorldChannels.currentChannel){
            WorldChannels.currentChannel.remove(event.getPlayer().getName());
         }
      }
   }
}
