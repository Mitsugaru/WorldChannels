package com.mitsugaru.worldchannels.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.chat.Channel;
import com.mitsugaru.worldchannels.services.WCModule;

public class ConfigHandler extends WCModule {
    private Map<String, WorldConfig> configs = new HashMap<String, WorldConfig>();
    private String formatterString, shoutFormat, nobodyString;
    private boolean formatterUse;
    public boolean hashQuickMessage, debugTime, debugVault, debugChat, debugAnnouncer;

    public ConfigHandler(WorldChannels plugin) {
        super(plugin);
    }

    @Override
    public void starting() {
        // Load defaults
        final ConfigurationSection config = plugin.getConfig();
        // LinkedHashmap of defaults
        final Map<String, Object> defaults = new LinkedHashMap<String, Object>();
        defaults.put("formatter.use", true);
        defaults.put("formatter.defaultFormat",
                "%world %group %prefix%name%suffix: %message");
        defaults.put("shout.format", "%prefix%name%suffix shouts: %message");
        defaults.put("nobody.message", "&oNo one can hear you...");
        defaults.put("hashQuickMessage", false);
        defaults.put("debug.time", false);
        defaults.put("debug.vault", false);
        defaults.put("debug.chat", false);
        defaults.put("debug.announcer", false);
        defaults.put("globalChannels", new ArrayList<String>());
        // Insert defaults into config file if they're not present
        for(final Entry<String, Object> e : defaults.entrySet()) {
            if(!config.contains(e.getKey())) {
                config.set(e.getKey(), e.getValue());
            }
        }
        //Update version
        config.set("version", plugin.getDescription().getVersion());
        // Save config
        plugin.saveConfig();
        // Load settings
        this.loadSettings(config);
        // Check if worlds folder exists
        final File file = new File(plugin.getDataFolder().getAbsolutePath()
                + "/worlds");
        if(!file.exists()) {
            // Create directory
            if(!file.mkdir()) {
                plugin.getLogger()
                        .warning(
                                "Something went wrong! Could not create worlds directory.");
            }
        }
        // Load config per world
        final List<World> worlds = plugin.getServer().getWorlds();
        for(World world : worlds) {
            final String worldName = world.getName();
            configs.put(worldName, new WorldConfig(plugin, worldName));
        }
        hookChannels();
    }

    public void reloadConfigs() {
        plugin.reloadConfig();
        for(WorldConfig config : configs.values()) {
            config.reload();
        }
        this.loadSettings(plugin.getConfig());
        hookChannels();
    }

    private void loadSettings(ConfigurationSection config) {
        /**
         * Formatter
         */
        formatterUse = config.getBoolean("formatter.use", true);
        formatterString = config.getString("formatter.defaultFormat",
                "%world %group %prefix%name%suffix: %message");
        /**
         * Shout
         */
        shoutFormat = config.getString("shout.format",
                "%prefix%name%suffix shouts: %message");
        /**
         * Nobody
         */
        nobodyString = config.getString("nobody.message",
                "&No one can hear you...");
        /**
         * Hash quick message
         */
        hashQuickMessage = config.getBoolean("hashQuickMessage", true);
        /**
         * Debug
         */
        debugTime = config.getBoolean("debug.time", false);
        debugVault = config.getBoolean("debug.vault", false);
        debugChat = config.getBoolean("debug.chat", false);
    }

    private void hookChannels() {
        for(WorldConfig config : configs.values()) {
            config.loadChannelHooks();
        }
    }

    public WorldConfig getWorldConfig(String worldName)
            throws IllegalArgumentException {
        WorldConfig out = configs.get(worldName);
        if(out == null) {
            throw new IllegalArgumentException(
                    "Missing configuration for world: " + worldName);
        }
        return out;
    }

    public boolean useFormatter() {
        return formatterUse;
    }

    public String getFormat() {
        return formatterString;
    }

    public String getShoutFormat() {
        return shoutFormat;
    }

    public String getNobodyMessage() {
        return nobodyString;
    }

    @Override
    public void closing() {
    }
}
