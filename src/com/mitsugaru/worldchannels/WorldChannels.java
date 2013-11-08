package com.mitsugaru.worldchannels;

import java.util.concurrent.ConcurrentHashMap;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mitsugaru.worldchannels.channels.Channel;
import com.mitsugaru.worldchannels.commands.Commander;
import com.mitsugaru.worldchannels.config.ConfigHandler;
import com.mitsugaru.worldchannels.config.LocalizeConfig;
import com.mitsugaru.worldchannels.events.WCPlayerListener;
import com.mitsugaru.worldchannels.events.WChatListener;
import com.mitsugaru.worldchannels.permissions.PermissionHandler;
import com.mitsugaru.worldchannels.tasks.PlayerChangedWorldTask;

public class WorldChannels extends JavaPlugin{
   private Chat chat = null;
   public static final String TAG = "[WorldChannels]";
   private ConfigHandler configHandler;
   public static final ConcurrentHashMap<String, Channel> currentChannel = new ConcurrentHashMap<String, Channel>();

   /**
    * Method that is called when plugin is enabled
    */
   @Override
   public void onEnable(){
      // Initialize configs
      configHandler = new ConfigHandler(this);
      configHandler.init();
      LocalizeConfig.init(this);
      // Grab Chat class from Vault
      final RegisteredServiceProvider<Chat> chatProvider = this.getServer()
            .getServicesManager().getRegistration(Chat.class);
      if(chatProvider != null){
         chat = chatProvider.getProvider();

      }else{
         // They don't have vault (or have an outdated version)
         this.getLogger().warning("Vault's Chat class not found! Will not be able to populate group/prefix/suffix entries!");
         chat = null;
      }
      // Setup permissions
      PermissionHandler.init(this);
      // Setup commander
      getCommand("wc").setExecutor(new Commander(this));
      // Setup listeners
      final PluginManager pm = this.getServer().getPluginManager();
      pm.registerEvents(new WChatListener(this), this);
      pm.registerEvents(new WCPlayerListener(this), this);
      //Setup tasks
      this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new PlayerChangedWorldTask(this), 100, 100);
   }

   public ConfigHandler getConfigHandler(){
      return configHandler;
   }

   public Chat getChat(){
      return chat;
   }

   /**
    * Thanks to Njol for the following method
    * http://forums.bukkit.org/threads/multiple
    * -classes-config-colours.79719/#post-1154761
    * 
    * @author Njol
    */
   public static String colorizeText(String string){
      return ChatColor.translateAlternateColorCodes('&', string);
   }
}
