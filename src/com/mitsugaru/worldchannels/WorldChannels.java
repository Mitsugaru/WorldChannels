package com.mitsugaru.worldchannels;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.mitsugaru.worldchannels.chat.Channel;
import com.mitsugaru.worldchannels.commands.Commander;
import com.mitsugaru.worldchannels.config.ConfigHandler;
import com.mitsugaru.worldchannels.config.localize.LocalizeConfig;
import com.mitsugaru.worldchannels.config.localize.Localizer;
import com.mitsugaru.worldchannels.events.WCPlayerListener;
import com.mitsugaru.worldchannels.events.WChatListener;
import com.mitsugaru.worldchannels.services.WCModule;
import com.mitsugaru.worldchannels.tasks.PlayerChangedWorldTask;

public class WorldChannels extends JavaPlugin {
    private Chat chat = null;
    public static final String TAG = "[WorldChannels]";
    public static final ConcurrentHashMap<String, Channel> currentChannel = new ConcurrentHashMap<String, Channel>();
    /**
     * Modules.
     */
    private final Map<Class<? extends WCModule>, WCModule> modules = new HashMap<Class<? extends WCModule>, WCModule>();

    /**
     * Method that is called when plugin is enabled
     */
    @Override
    public void onEnable() {
        // Initialize modules
        registerModule(ConfigHandler.class, new ConfigHandler(this));
        registerModule(LocalizeConfig.class, new LocalizeConfig(this));

        // Grab Chat class from Vault
        final RegisteredServiceProvider<Chat> chatProvider = this.getServer()
                .getServicesManager().getRegistration(Chat.class);
        if(chatProvider != null) {
            chat = chatProvider.getProvider();

        } else {
            // They don't have vault (or have an outdated version)
            this.getLogger()
                    .warning(
                            "Vault's Chat class not found! Will not be able to populate group/prefix/suffix entries!");
            chat = null;
        }

        // Setup localizer util class
        Localizer.setPlugin(this);

        // Setup commander
        getCommand("wc").setExecutor(new Commander(this));

        // Setup listeners
        final PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new WChatListener(this), this);
        pm.registerEvents(new WCPlayerListener(this), this);

        // Setup tasks
        this.getServer()
                .getScheduler()
                .scheduleSyncRepeatingTask(this,
                        new PlayerChangedWorldTask(this), 100, 100);
    }

    public Chat getChat() {
        return chat;
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

    /**
     * Register a CCModule to the API.
     * 
     * @param clazz
     *            - Class of the instance.
     * @param module
     *            - Module instance.
     * @throws IllegalArgumentException
     *             - Thrown if an argument is null.
     */
    public <T extends WCModule> void registerModule(Class<T> clazz, T module) {
        // Check arguments.
        if(clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        } else if(module == null) {
            throw new IllegalArgumentException("Module cannot be null");
        }
        // Add module.
        modules.put(clazz, module);
        // Tell module to start.
        module.starting();
    }

    /**
     * Unregister a CCModule from the API.
     * 
     * @param clazz
     *            - Class of the instance.
     * @return Module that was removed from the API. Returns null if no instance
     *         of the module is registered with the API.
     */
    public <T extends WCModule> T deregisterModuleForClass(Class<T> clazz) {
        // Check arguments.
        if(clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        // Grab module and tell it its closing.
        T module = clazz.cast(modules.get(clazz));
        if(module != null) {
            module.closing();
        }
        return module;
    }

    /**
     * Retrieve a registered CCModule.
     * 
     * @param clazz
     *            - Class identifier.
     * @return CCModule instance. Returns null is an instance of the given class
     *         has not been registered with the API.
     */
    public <T extends WCModule> T getModuleForClass(Class<T> clazz) {
        return clazz.cast(modules.get(clazz));
    }
}
