package com.mitsugaru.worldchannels.events;

import java.util.List;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.channels.Channel;
import com.mitsugaru.worldchannels.config.WorldConfig;

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
         for(Channel channel : config.getChannels()){
            if(channel.isAutoJoin()){
               channel.addListener(event.getPlayer().getName());
            }
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
         final List<World> worlds = plugin.getServer().getWorlds();
         for(World world : worlds){
            final WorldConfig config = plugin.getConfigHandler()
                  .getWorldConfig(world.getName());
            for(Channel channel : config.getChannels()){
               channel.removeListener(event.getPlayer().getName());
               channel.removeObserver(event.getPlayer().getName());
            }
         }
      }
   }
}
