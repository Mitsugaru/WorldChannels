package com.mitsugaru.worldchannels.events;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.mitsugaru.worldchannels.WChat;
import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.WChat.Field;
import com.mitsugaru.worldchannels.channels.Channel;
import com.mitsugaru.worldchannels.config.ConfigHandler;
import com.mitsugaru.worldchannels.config.WorldConfig;
import com.mitsugaru.worldchannels.permissions.PermissionHandler;
import com.mitsugaru.worldchannels.permissions.PermissionNode;

public class WChatListener implements Listener{
   private WorldChannels plugin;
   private ConfigHandler configHandler;

   public WChatListener(WorldChannels plugin){
      this.plugin = plugin;
      this.configHandler = plugin.getConfigHandler();
   }

   /**
    * Handle hashtag quick message events
    * 
    * @param event
    *           - AsyncPlayerChatEvent that occurred
    */
   @EventHandler(priority = EventPriority.LOWEST)
   public void hashMessage(final AsyncPlayerChatEvent event){
      if(event.getPlayer() == null || event.getMessage() == null
            || !configHandler.hashQuickMessage || event.isCancelled()){
         return;
      }else if(event.getPlayer().getWorld() == null){
         return;
      }
      if(event.getMessage().charAt(0) != '#'){
         return;
      }
      // Hash message
      boolean ours = false;
      Channel target = null;
      final String userTag = event.getMessage().split(" ")[0].replace("#", "");
      // Check world channels
      WorldConfig conf;
      try{
         conf = configHandler.getWorldConfig(event.getPlayer().getWorld()
               .getName());
      }catch(IllegalArgumentException e){
         plugin.getLogger().log(Level.WARNING, e.getMessage(), e);
         return;
      }
      for(Channel channel : conf.getChannels()){
         if(channel.getTag().equalsIgnoreCase(userTag)){
            ours = true;
            target = channel;
         }
      }
      // Check global channels, if not found
      if(!ours){
         for(Channel channel : configHandler.getGlobalChannels()){
            if(channel.getTag().equalsIgnoreCase(userTag)){
               ours = true;
               target = channel;
            }
         }
      }
      if(ours){
         event.setMessage(event.getMessage().replace(
               event.getMessage().split(" ")[0], ""));
         // Handle text to channel recepients
         handleChatEvent(event, conf, target);
      }
   }

   /**
    * Listen for ChatEvents to change who listens to it. Set to HIGHEST priority
    * so that other chat plugins can do what they need to do to the
    * message/format.
    * 
    * @param event
    *           - AsyncPlayerChatEvent that occurred
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void chatEvent(final AsyncPlayerChatEvent event){
      // Don't care about event if it is cancelled
      if(event.isCancelled() || event.getPlayer() == null){
         return;
      }
      if(event.getMessage().charAt(0) == '#'){
         // Ignore, as we handled it earlier
         return;
      }
      // Grab player
      final Player player = event.getPlayer();
      if(event.getPlayer().getWorld() == null){
         return;
      }
      // Get world name
      final String worldName = event.getPlayer().getWorld().getName();
      // Grab world specific config
      final WorldConfig config = plugin.getConfigHandler().getWorldConfig(
            worldName);

      Channel channel = null;
      synchronized (WorldChannels.currentChannel){
         channel = WorldChannels.currentChannel.get(player.getName());
      }
      if(channel == null){
         // Grab default of the world
         channel = config.getDefaultChannel();
      }
      
      Set<Player> receivers = new HashSet<Player>();
      if(channel.includeWorldPlayers()){
         // Add people of the original world
         final CopyOnWriteArrayList<Player> playerList = new CopyOnWriteArrayList<Player>();
         synchronized (player.getWorld().getPlayers()){
            playerList.addAll(player.getWorld().getPlayers());
         }
         receivers.addAll(playerList);
      }
      
      handleChatEvent(event, config, channel);
   }

   private void handleChatEvent(final AsyncPlayerChatEvent event,
         WorldConfig config, Channel channel){
      // Grab player
      final Player player = event.getPlayer();
      // Check mute
      if(channel.getMuted().contains(player.getName())){
         player.sendMessage("You are muted for channel '" + channel.getName()
               + "' in world '" + config.getWorldName() + "'");
         event.setCancelled(true);
         return;
      }
      // Get world name
      final String worldName = event.getPlayer().getWorld().getName();
      Set<Player> receivers = new HashSet<Player>();
      if(channel.includeWorldPlayers()){
         // Add people of the original world
         receivers.addAll(event.getPlayer().getWorld().getPlayers());
      }
      // Add all listeners from each linked channel
      for(Channel linked : channel.getRecievingChannels()){
         for(String name : linked.getListeners()){
            final Player linkedReceiver = plugin.getServer().getPlayer(name);
            if(linkedReceiver != null){
               receivers.add(linkedReceiver);
            }
         }
      }
      // Check if we're going to use local
      if(channel.isLocal()){
         final CopyOnWriteArrayList<Entity> entityList = new CopyOnWriteArrayList<Entity>();
         entityList.addAll(player.getNearbyEntities(
               channel.getRadius(), channel.getRadius(),
               channel.getRadius()));
         for(Entity entity : entityList){
            if(entity instanceof Player){
               receivers.add((Player) entity);
            }
         }
      }
      boolean empty = false;
      if(receivers.isEmpty()){
         empty = true;
      }else if(receivers.size() == 1 && receivers.contains(player)){
         empty = true;
      }
      // Add player to receivers by default
      receivers.add(player);
      // Add observers
      for(String observer : channel.getObservers()){
         final Player playerObserver = plugin.getServer().getPlayer(observer);
         if(playerObserver != null && playerObserver.isOnline()){
            receivers.add(playerObserver);
         }
      }
      // Clear recipients
      event.getRecipients().clear();
      // Add our receivers
      event.getRecipients().addAll(receivers);
      if(empty){
         if(config.useNobody()){
            player.sendMessage(WChat.parseString(config.getNobodyMessage(),
                  null));
         }else{
            player.sendMessage(WChat.parseString(
                  configHandler.getNobodyMessage(), null));
         }
      }
      // Set info of fields for formatting message and format
      final EnumMap<Field, String> info = new EnumMap<Field, String>(
            Field.class);
      info.put(Field.NAME, "%1\\$s");
      info.put(Field.WORLD, worldName);
      String group = "";
      try{
         group = plugin.getChat().getPlayerGroups(player)[0];
      }catch(ArrayIndexOutOfBoundsException a){
         // IGNORE
      }catch(NullPointerException npe){
         group = "";
         if(configHandler.debugVault){
            plugin.getLogger().warning(
                  "Vault threw NPE... Could not retrieve group name!");
         }
      }
      info.put(Field.GROUP, group);
      String prefix = "";
      try{
         prefix = plugin.getChat().getPlayerPrefix(worldName, player.getName());
      }catch(NullPointerException npe){
         prefix = "";
         if(configHandler.debugVault){
            plugin.getLogger().warning(
                  "Vault threw NPE... Could not retrieve prefix!");
         }
      }
      info.put(Field.PREFIX, prefix);
      String suffix = "";
      try{
         suffix = plugin.getChat().getPlayerSuffix(worldName, player.getName());
      }catch(NullPointerException npe){
         suffix = "";
         if(configHandler.debugVault){
            plugin.getLogger().warning(
                  "Vault threw NPE... Could not retrieve suffix!");
         }
      }
      info.put(Field.SUFFIX, suffix);
      info.put(Field.MESSAGE, "%2\\$s");
      // Check if we are going to edit the format at all
      if(channel.isFormat()){
         final String format = channel.getFormatterString();
         if(!format.equals("")){
            event.setFormat(WChat.parseString(format, info));
         }
      }else if(config.useFormatter()){
         final String format = config.getFormat();
         if(!format.equals("")){
            event.setFormat(WChat.parseString(format, info));
         }
      }else if(configHandler.useFormatter()){
         final String format = configHandler.getFormat();
         if(!format.equals("")){
            event.setFormat(WChat.parseString(format, info));
         }
      }
      // Check if we colorize their chat
      if(PermissionHandler.checkPermission(player,
            PermissionNode.COLORIZE.getNode())){
         event.setMessage(WorldChannels.colorizeText(event.getMessage()));
      }
   }
}
