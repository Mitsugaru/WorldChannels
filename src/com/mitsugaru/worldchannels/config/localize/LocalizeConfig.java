package com.mitsugaru.worldchannels.config.localize;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.services.WCModule;

public class LocalizeConfig extends WCModule {
    
    // Class variables
    private File file;
    private YamlConfiguration config;
    private final Map<LocalString, String> values = new EnumMap<LocalString, String>(LocalString.class);
    
    public LocalizeConfig(WorldChannels plugin) {
        super(plugin);
    }

    public void save() {
        // Set config
        try {
            // Save the file
            config.save(file);
        } catch(IOException e1) {
            plugin.getLogger().warning(
                    "File I/O Exception on saving localization config");
            e1.printStackTrace();
        }
    }

    public void reload() {
        try {
            config.load(file);
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InvalidConfigurationException e) {
            e.printStackTrace();
        }
        loadDefaults();
        loadVariables();
    }

    private void loadDefaults() {
        // LinkedHashmap of defaults
        final Map<String, String> defaults = new LinkedHashMap<String, String>();
        // defaults for all strings
        defaults.put("message.observer.on", "&a%tag Observer mode on");
        defaults.put("message.observer.off", "&a%tag Observer mode off");
        defaults.put("message.noPermission", "&c%tag Lack permission: %extra");
        defaults.put("message.reloadConfig", "&a%tag &fConfig reloaded.");
        defaults.put("message.unknown", "&c%tag Unknown %reason '&6%extra&c'");
        defaults.put("message.noConsole",
                "&a%tag Cannot use this command as console");
        defaults.put("message.missingParam", "&a%tag Missing parameter: %extra");
        defaults.put("help.help", "&a/wc help&e : Show help menu");
        defaults.put("help.shout", "&a/wc shout <message...>&e : Shout message");
        defaults.put("help.observe", "&a/wc observe &e: Toggle observe mode");
        defaults.put("help.admin.reload",
                "&a/wc reload&e : Reload all config files");
        defaults.put("help.version",
                "&a/wc version&e : Show version and config");
        defaults.put("reason.limit", "Hit limit");
        defaults.put("reason.money", "Lack money");
        defaults.put("reason.unknown", " Unknown DenyType");
        // TODO debug messages
        // Add to config if missing
        for(final Entry<String, String> e : defaults.entrySet()) {
            if(!config.contains(e.getKey())) {
                config.set(e.getKey(), e.getValue());
            }
        }
        save();
    }

    private void loadVariables() {
        // load variables
        /**
         * Messages
         */
        values.put(LocalString.PERMISSION_DENY, config.getString("message.noPermission",
                "&c%tag Lack permission: %extra"));
        values.put(LocalString.RELOAD_CONFIG, config.getString("message.reloadConfig",
                "&a%tag &fConfig reloaded."));
        values.put(LocalString.OBSERVER_ON,config.getString("message.observer.on",
                "&a%tag Observer mode on"));
        values.put(LocalString.OBSERVER_OFF, config.getString("message.observer.off",
                "&a%tag Observer mode off"));
        values.put(LocalString.NO_CONSOLE,config.getString("message.noConsole",
                "&a%tag Cannot use this command as console"));
        values.put(LocalString.MISSING_PARAM, config.getString("message.missingParam",
                "&a%tag Missing %extra parameter"));
        values.put(LocalString.UNKNOWN, config.getString("message.unknown",
                "&c%tag Unknown %reason '&6%extra&c'"));
        /**
         * help
         */
        values.put(LocalString.HELP_SHOUT, config.getString("help.shout",
                "&a/wc shout <message...>&e : Shout message"));
        values.put(LocalString.HELP_HELP, config.getString("help.help",
                "&a/wc help&e : Show help menu"));
        values.put(LocalString.HELP_OBSERVE, config.getString("help.observe",
                "&a/wc observe &e: Toggle observe mode"));
        values.put(LocalString.HELP_ADMIN_RELOAD, config.getString("help.admin.reload",
                "&a/wc reload&e : Reload all config files"));
        values.put(LocalString.HELP_VERSION, config.getString("help.version",
                "&a/wc version&e : Show version and config"));
    }
    
    public String getMessage(LocalString target) {
        String out = values.get(target);
        if(out == null) {
            out = "";
        }
        return out;
    }

    @Override
    public void starting() {
        file = new File(plugin.getDataFolder().getAbsolutePath()
                + "/localization.yml");
        config = YamlConfiguration.loadConfiguration(file);
        loadDefaults();
        loadVariables();
    }

    @Override
    public void closing() {
        save();
    }
}
