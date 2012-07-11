package com.mitsugaru.WorldChannels;

import java.util.HashSet;
import java.util.Set;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mitsugaru.WorldChannels.config.ConfigHandler;
import com.mitsugaru.WorldChannels.config.LocalizeConfig;
import com.mitsugaru.WorldChannels.events.McMMOListener;
import com.mitsugaru.WorldChannels.events.WChatListener;
import com.mitsugaru.WorldChannels.permissions.PermissionHandler;

public class WorldChannels extends JavaPlugin {
    private Chat chat;
    public static final String TAG = "[WorldChannels]";
    private ConfigHandler configHandler;
    private boolean mcmmo;
    public static final Set<String> observers = new HashSet<String>();
    public static final Set<String> mcmmoChat = new HashSet<String>();

    /**
     * Method that is called when plugin is enabled
     */
    @Override
    public void onEnable() {
	// Initialize configs
	configHandler = new ConfigHandler(this);
	LocalizeConfig.init(this);
	// Grab Chat class from Vault
	final RegisteredServiceProvider<Chat> chatProvider = this.getServer()
		.getServicesManager().getRegistration(Chat.class);
	if (chatProvider != null) {
	    chat = chatProvider.getProvider();
	    // Setup permissions
	    PermissionHandler.init(this);
	    // Setup commander
	    getCommand("wc").setExecutor(new Commander(this));
	    // Setup listeners
	    final PluginManager pm = this.getServer().getPluginManager();
	    pm.registerEvents(new WChatListener(this), this);
	    Plugin mcmmoPlugin = getServer().getPluginManager().getPlugin(
		    "mcMMO");
	    if (mcmmoPlugin != null) {
		McMMOListener mcmmoListener = new McMMOListener();
		this.getServer().getPluginManager()
			.registerEvents(mcmmoListener, this);
		mcmmo = true;
		getLogger().info("Hooked into mcMMO");
	    } else {
		mcmmo = false;
	    }
	} else {
	    // They don't have vault (or have an outdated version)
	    this.getLogger().warning(
		    "Vault's Chat class not found! Disabling...");
	    this.getServer().getPluginManager().disablePlugin(this);
	}
    }

    public ConfigHandler getConfigHandler() {
	return configHandler;
    }

    public Chat getChat() {
	return chat;
    }

    public boolean hasMcMMO() {
	return mcmmo;
    }

    /**
     * Thanks to Njol for the following method
     * http://forums.bukkit.org/threads/multiple
     * -classes-config-colours.79719/#post-1154761
     * 
     * @author Njol
     */
    public static String colorizeText(String string) {
	return ChatColor.translateAlternateColorCodes('&', string);
    }
}
