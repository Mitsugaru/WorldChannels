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
import com.mitsugaru.worldchannels.channels.Channel;

public class ConfigHandler {
    private WorldChannels plugin;
    private Map<String, WorldConfig> configs = new HashMap<String, WorldConfig>();
    private String formatterString, shoutFormat, nobodyString;
    private boolean formatterUse;
    public boolean hashQuickMessage, debugTime, debugVault;
    private Map<String, Channel> globalChannels = new HashMap<String, Channel>();

    public ConfigHandler(WorldChannels plugin) {
        this.plugin = plugin;
    }

    public void init() {
        // Load defaults
        final ConfigurationSection config = plugin.getConfig();
        // LinkedHashmap of defaults
        final Map<String, Object> defaults = new LinkedHashMap<String, Object>();
        defaults.put("formatter.use", true);
        defaults.put("formatter.defaultFormat",
                "%world %group %prefix%name%suffix: %message");
        defaults.put("shout.format", "%prefix%name%suffix shouts: %message");
        defaults.put("nobody.message", "&oNo one can hear you...");
        defaults.put("hashQuickMessage", true);
        defaults.put("debug.time", false);
        defaults.put("debug.vault", false);
        defaults.put("version", plugin.getDescription().getVersion());
        defaults.put("globalChannels", new ArrayList<String>());
        // Insert defaults into config file if they're not present
        for(final Entry<String, Object> e : defaults.entrySet()) {
            if(!config.contains(e.getKey())) {
                config.set(e.getKey(), e.getValue());
            }
        }
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
        loadGlobalChannels();
        hookGlobalChannels();
        hookChannels();
    }

    public void reloadConfigs() {
        plugin.reloadConfig();
        for(WorldConfig config : configs.values()) {
            config.reload();
        }
        this.loadSettings(plugin.getConfig());
        loadGlobalChannels();
        hookGlobalChannels();
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

    private void loadGlobalChannels() {
        globalChannels.clear();
        ConfigurationSection section = plugin.getConfig()
                .getConfigurationSection("globalChannels");
        if(section == null) {
            return;
        }
        for(String channelName : section.getKeys(false)) {
            final String tag = section.getString(channelName + ".tag", "g");
            final Channel channel = new Channel(tag, channelName, "GLOBAL");
            channel.setGlobal(true);
            /**
             * get channel settings
             */
            // local
            boolean localUse = section.getBoolean(channelName + ".local.use",
                    false);
            int localRadius = section
                    .getInt(channelName + ".local.radius", 100);
            if(localRadius <= 0) {
                localRadius = 100;
            }
            channel.setLocal(localUse);
            channel.setRadius(localRadius);
            // nobody
            boolean nobodyUse = section.getBoolean(channelName + ".nobody.use",
                    false);
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
                    + ".permission.join", "WorldChannels.globalchannel."
                    + channelName + ".join");
            String permissionLeave = section.getString(channelName
                    + ".permission.leave", "WorldChannels.globalchannel."
                    + channelName + ".leave");
            String permissionKick = section.getString(channelName
                    + ".permission.kick", "WorldChannels.globalchannel."
                    + channelName + ".kick");
            String permissionMute = section.getString(channelName
                    + ".permission.mute", "WorldChannels.globalchannel."
                    + channelName + ".mute");
            channel.setPermissionJoin(permissionJoin);
            channel.setPermissionLeave(permissionLeave);
            channel.setPermissionMute(permissionMute);
            channel.setPermissionKick(permissionKick);
            try {
                plugin.getServer().getPluginManager()
                        .addPermission(new Permission(permissionJoin));
            } catch(IllegalArgumentException e) {
                // Ignore
            }
            try {
                plugin.getServer().getPluginManager()
                        .addPermission(new Permission(permissionLeave));
            } catch(IllegalArgumentException e) {
                // Ignore
            }
            try {
                plugin.getServer().getPluginManager()
                        .addPermission(new Permission(permissionMute));
            } catch(IllegalArgumentException e) {
                // Ignore
            }
            try {
                plugin.getServer().getPluginManager()
                        .addPermission(new Permission(permissionKick));
            } catch(IllegalArgumentException e) {
                // Ignore
            }
            // Add channel
            globalChannels.put(channelName, channel);
        }
    }

    private void hookGlobalChannels() {
        for(Map.Entry<String, Channel> entry : globalChannels.entrySet()) {
            if(!(plugin.getConfig().contains("globalChannels." + entry.getKey()
                    + ".linkedChannels"))) {
                continue;
            }
            List<String> links = plugin.getConfig().getStringList(
                    "globalChannels." + entry.getKey() + ".linkedChannels");
            for(String link : links) {
                if(link.contains(":")) {
                    final String[] split = link.split(":");
                    // Other world
                    WorldConfig otherWorld;
                    try {
                        otherWorld = plugin.getConfigHandler().getWorldConfig(
                                split[0]);
                    } catch(IllegalArgumentException e) {
                        plugin.getLogger()
                                .log(Level.WARNING, e.getMessage(), e);
                        continue;
                    }
                    if(otherWorld != null) {
                        final Channel otherChannel = otherWorld
                                .getChannel(split[1]);
                        if(otherChannel != null) {
                            entry.getValue().addChannel(otherChannel);
                        } else {
                            plugin.getLogger()
                                    .warning(
                                            "Link channel '"
                                                    + split[1]
                                                    + "' of other world '"
                                                    + split[0]
                                                    + "' not found for global channel '"
                                                    + entry.getKey() + "'");
                        }
                    } else {
                        plugin.getLogger().warning(
                                "Other world '" + split[0]
                                        + "' not found for global channel '"
                                        + entry.getKey() + "'");
                    }
                } else if(globalChannels.containsKey(link)) {
                    // local world channel to hook to
                    entry.getValue().addChannel(globalChannels.get(link));
                } else {
                    // Invalid entry
                    plugin.getLogger().warning(
                            "Invalid link channel entry '" + link
                                    + "' for global channel '" + entry.getKey()
                                    + "'");
                }
            }
        }
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

    public Collection<Channel> getGlobalChannels() {
        return globalChannels.values();
    }
}
