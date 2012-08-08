package com.mitsugaru.WorldChannels.events;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.mitsugaru.WorldChannels.WChat;
import com.mitsugaru.WorldChannels.WChat.Field;
import com.mitsugaru.WorldChannels.WorldChannels;
import com.mitsugaru.WorldChannels.config.WorldConfig;
import com.mitsugaru.WorldChannels.config.ConfigHandler;
import com.mitsugaru.WorldChannels.permissions.PermissionHandler;
import com.mitsugaru.WorldChannels.permissions.PermissionNode;

public class WChatListener implements Listener{
   private WorldChannels plugin;
   private ConfigHandler configHandler;

   public WChatListener(WorldChannels plugin){
      this.plugin = plugin;
      this.configHandler = plugin.getConfigHandler();
   }

   /**
    * Listen for ChatEvents to change who listens to it. Set to HIGHEST priority
    * so that other chat plugins can do what they need to do to the
    * message/format.
    * 
    * @param AsyncPlayerChatEvent
    */
   @EventHandler(priority = EventPriority.HIGHEST)
   public void chatEvent(final AsyncPlayerChatEvent event){
      // Don't care about event if it is cancelled
      if(event.isCancelled() || event.getPlayer() == null){
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

      Set<Player> receivers = new HashSet<Player>();
      if(config.includeLocalPlayers()){
         // Add people of the original world
         final CopyOnWriteArrayList<Player> playerList = new CopyOnWriteArrayList<Player>();
         playerList.addAll(player.getWorld().getPlayers());
         receivers.addAll(playerList);
      }
      // Grab list
      final Set<String> worldList = plugin.getConfigHandler().getWorldChannels(
            worldName);
      // Check if empty. If empty, we don't add any other world checks
      if(!worldList.isEmpty()){
         for(String name : worldList){
            final World world = plugin.getServer().getWorld(name);
            if(world == null){
               continue;
            }
            final CopyOnWriteArrayList<Player> playerList = new CopyOnWriteArrayList<Player>();
            playerList.addAll(player.getWorld().getPlayers());
            receivers.addAll(playerList);
         }
      }
      // Check if we're going to use local
      if(config.useLocal()){
         final CopyOnWriteArrayList<Entity> entityList = new CopyOnWriteArrayList<Entity>();
         entityList.addAll(player.getNearbyEntities(
               config.getLocalRadius(), config.getLocalRadius(),
               config.getLocalRadius()));
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
      for(String observer : WorldChannels.observers){
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
      }
      catch(NullPointerException npe)
      {
         group = "";
         if(configHandler.debugVault)
         {
            plugin.getLogger().warning("Vault threw NPE... Could not retrieve group name!");
         }
      }
      info.put(Field.GROUP, group);
      String prefix = "";
      try{
         prefix = plugin.getChat().getPlayerPrefix(worldName, player.getName());
      }catch(NullPointerException npe){
         prefix = "";
         if(configHandler.debugVault)
         {
            plugin.getLogger().warning("Vault threw NPE... Could not retrieve prefix!");
         }
      }
      info.put(Field.PREFIX, prefix);
      String suffix = "";
      try{
         suffix = plugin.getChat().getPlayerSuffix(worldName, player.getName());
      }catch(NullPointerException npe){
         suffix = "";
         if(configHandler.debugVault)
         {
            plugin.getLogger().warning("Vault threw NPE... Could not retrieve suffix!");
         }
      }
      info.put(Field.SUFFIX, suffix);
      info.put(Field.MESSAGE, "%2\\$s");
      // Check if we are going to edit the format at all
      if(config.useFormatter()){
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
