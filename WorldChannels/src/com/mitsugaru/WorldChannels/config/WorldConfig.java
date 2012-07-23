package com.mitsugaru.WorldChannels.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;

import com.mitsugaru.WorldChannels.WorldChannels;
import com.mitsugaru.WorldChannels.channels.Channel;
import com.mitsugaru.WorldChannels.tasks.WorldAnnouncerTask;

public class WorldConfig{
   private String worldName, formatterString, nobodyString, channelTag;
   private WorldChannels plugin;
   private File file;
   private YamlConfiguration config;
   private boolean formatterUse, announcerUse, nobodyUse;
   private static final int minutesToTicks = 1200;
   private int announcerInterval = 15, announcerId = -1;
   private List<String> announcements = new ArrayList<String>();
   private Map<String, Channel> channels = new HashMap<String, Channel>();
   private Channel defaultChannel;

   public WorldConfig(WorldChannels plugin, String worldName){
      this.plugin = plugin;
      this.worldName = worldName;
      // Grab file
      this.file = new File(plugin.getDataFolder().getAbsolutePath()
            + "/worlds/" + worldName + ".yml");
      if(!file.exists()){
         try{
            file.createNewFile();
         }catch(IOException e){
            plugin.getLogger().severe(
                  "Could not create config file for world: " + worldName);
            e.printStackTrace();
         }
      }
      this.config = YamlConfiguration.loadConfiguration(file);
      reload();
   }

   public void save(){
      // Set config
      try{
         // Save the file
         config.save(file);
      }catch(IOException e1){
         plugin.getLogger().warning(
               "File I/O Exception on saving heroes config");
         e1.printStackTrace();
      }
   }

   public void reload(){
      try{
         config.load(file);
      }catch(FileNotFoundException e){
         plugin.getLogger().severe(
               "Could not find config file for world: " + worldName);
         e.printStackTrace();
      }catch(IOException e){
         plugin.getLogger().severe(
               "IOException for config file for world: " + worldName);
         e.printStackTrace();
      }catch(InvalidConfigurationException e){
         plugin.getLogger().severe(
               "Invalid config file for world: " + worldName);
         e.printStackTrace();
      }
      loadDefaults();
      loadVariables();
      loadChannels();
      boundsCheck();
      startAnnouncer();
   }

   private void loadDefaults(){
      // LinkedHashmap of defaults
      final Map<String, Object> defaults = new LinkedHashMap<String, Object>();
      defaults.put("formatter.use", false);
      defaults.put("formatter.format",
            "%world %group %prefix%name%suffix: %message");
      defaults.put("announcer.use", false);
      defaults.put("announcer.interval", 15);
      defaults.put("announcer.annoucements", new ArrayList<String>());
      defaults.put("nobody.use", false);
      defaults.put("nobody.message", "&oNo one can hear you...");
      defaults.put("tag", "");
      defaults.put("channels", new ArrayList<String>());

      // Add to config if missing
      for(final Entry<String, Object> e : defaults.entrySet()){
         if(!config.contains(e.getKey())){
            config.set(e.getKey(), e.getValue());
         }
      }
      save();
   }

   private void loadVariables(){
      // load variables
      formatterUse = config.getBoolean("formatter.use", false);
      formatterString = config.getString("formatter.format",
            "%world %group %prefix%name%suffix: %message");
      announcerUse = config.getBoolean("announcer.use", false);
      announcerInterval = config.getInt("announcer.interval", 15);
      announcements = config.getStringList("announcer.annoucements");
      nobodyUse = config.getBoolean("nobody.use", false);
      nobodyString = config.getString("nobody.message",
            "&oNo one can hear you...");
      channelTag = config.getString("tag", "");
   }

   private void boundsCheck(){
      if(announcerInterval <= 0){
         announcerInterval = 15;
      }
      if(announcements == null){
         announcements = new ArrayList<String>();
      }
      // Check default channel
      if(defaultChannel == null){
         if(channels.isEmpty()){
            // Create a default channel
            defaultChannel = new Channel("[def]", "Default", worldName);
            defaultChannel.setIncludeWorldPlayers(true);
            defaultChannel.setAutoJoin(true);
            channels.put("Default", defaultChannel);
         }else{
            // Choose first channel as default channel, if none was specified
            defaultChannel = channels.values().toArray(new Channel[0])[0];
         }
      }
   }

   private void startAnnouncer(){
      if(announcerId != -1){
         // Stop previous announcer
         plugin.getServer().getScheduler().cancelTask(announcerId);
      }
      if(!announcerUse || announcements.isEmpty()){
         return;
      }
      final int delay = announcerInterval * minutesToTicks;
      announcerId = plugin
            .getServer()
            .getScheduler()
            .scheduleSyncRepeatingTask(plugin,
                  new WorldAnnouncerTask(worldName, announcements), delay,
                  delay);
   }

   private void loadChannels(){
      channels.clear();
      ConfigurationSection section = config.getConfigurationSection("channels");
      if(section == null){
         return;
      }
      for(String channelName : section.getKeys(false)){
         final String tag = section.getString(channelName + ".tag", channelTag);
         final Channel channel = new Channel(tag, channelName, worldName);
         /**
          * get channel settings
          */
         // local
         boolean localUse = section.getBoolean(channelName + ".local.use",
               false);
         int localRadius = section.getInt(channelName + ".local.radius", 100);
         if(localRadius <= 0){
            localRadius = 100;
         }
         channel.setLocal(localUse);
         channel.setRadius(localRadius);
         // nobody
         boolean nobodyUse = section.getBoolean(channelName + ".nobody.use",
               this.nobodyUse);
         String nobodyString = section.getString(channelName
               + ".nobody.message", this.nobodyString);
         channel.setNobody(nobodyUse);
         channel.setNobodyString(nobodyString);
         // Formatter
         boolean formatterUse = section.getBoolean(channelName
               + ".formatter.use", this.formatterUse);
         String formatterString = section.getString(channelName
               + ".formatter.format", this.formatterString);
         channel.setFormat(formatterUse);
         channel.setFormatterString(formatterString);
         // Autojoin
         boolean auto = section.getBoolean(channelName + ".autojoin", false);
         channel.setAutoJoin(auto);
         // world players
         boolean worldPlayers = section.getBoolean(channelName
               + ".includeLocalWorldPlayers", false);
         channel.setIncludeWorldPlayers(worldPlayers);
         // Register permissions
         String permissionJoin = section.getString(channelName
               + ".permission.join", "WorldChannels." + worldName + "."
               + channelName + ".join");
         String permissionLeave = section.getString(channelName
               + ".permission.leave", "WorldChannels." + worldName + "."
               + channelName + ".leave");
         String permissionKick = section.getString(channelName
               + ".permission.kick", "WorldChannels." + worldName + "."
               + channelName + ".kick");
         String permissionMute = section.getString(channelName
               + ".permission.mute", "WorldChannels." + worldName + "."
               + channelName + ".mute");
         channel.setPermissionJoin(permissionJoin);
         channel.setPermissionLeave(permissionLeave);
         channel.setPermissionMute(permissionMute);
         channel.setPermissionKick(permissionKick);
         try{
            plugin.getServer().getPluginManager()
                  .addPermission(new Permission(permissionJoin));
         }catch(IllegalArgumentException e){
            // Ignore
         }
         try{
            plugin.getServer().getPluginManager()
                  .addPermission(new Permission(permissionLeave));
         }catch(IllegalArgumentException e){
            // Ignore
         }
         try{
            plugin.getServer().getPluginManager()
                  .addPermission(new Permission(permissionMute));
         }catch(IllegalArgumentException e){
            // Ignore
         }
         try{
            plugin.getServer().getPluginManager()
                  .addPermission(new Permission(permissionKick));
         }catch(IllegalArgumentException e){
            // Ignore
         }
         // set default Channel
         if(section.getBoolean(channelName + ".default", false)){
            defaultChannel = channel;
         }
         // Add channel
         channels.put(channelName, channel);
      }
   }

   public void loadChannelHooks(){
      for(Map.Entry<String, Channel> entry : channels.entrySet()){
         if(!(config.contains("channels." + entry.getKey() + ".linkedChannels"))){
            continue;
         }
         List<String> links = config.getStringList("channels." + entry.getKey()
               + ".linkedChannels");
         for(String link : links){
            if(link.contains(":")){
               final String[] split = link.split(":");
               // Other world
               WorldConfig otherWorld;
               try{
                  otherWorld = plugin.getConfigHandler().getWorldConfig(
                        split[0]);
               }catch(IllegalArgumentException e){
                  plugin.getLogger().log(Level.WARNING, e.getMessage(), e);
                  continue;
               }
               if(otherWorld != null){
                  final Channel otherChannel = otherWorld.getChannel(split[1]);
                  if(otherChannel != null){
                     entry.getValue().addChannel(otherChannel);
                  }else{
                     plugin.getLogger().warning(
                           "Link channel '" + split[1] + "' of other world '"
                                 + split[0] + "' not found for channel '"
                                 + entry.getKey() + "' of world '" + worldName
                                 + "'");
                  }
               }else{
                  plugin.getLogger().warning(
                        "Other world '" + split[0]
                              + "' not found for channel '" + entry.getKey()
                              + "' of world '" + worldName + "'");
               }
            }else if(channels.containsKey(link)){
               // local world channel to hook to
               entry.getValue().addChannel(channels.get(link));
            }else{
               // Invalid entry
               plugin.getLogger().warning(
                     "Invalid link channel entry '" + link + "' for channel '"
                           + entry.getKey() + "' of world '" + worldName + "'");
            }
         }
      }
   }

   public Collection<Channel> getChannels(){
      return channels.values();
   }

   public Channel getChannel(String channelName){
      return channels.get(channelName);
   }

   public Channel getDefaultChannel(){
      return defaultChannel;
   }

   public String getWorldName(){
      return worldName;
   }

   public boolean useFormatter(){
      return formatterUse;
   }

   public String getFormat(){
      return formatterString;
   }

   public boolean useNobody(){
      return nobodyUse;
   }

   public String getNobodyMessage(){
      return nobodyString;
   }
}
