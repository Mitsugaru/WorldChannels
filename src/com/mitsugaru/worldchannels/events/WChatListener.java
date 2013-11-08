package com.mitsugaru.worldchannels.events;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.chat.Channel;
import com.mitsugaru.worldchannels.chat.ChannelManager;
import com.mitsugaru.worldchannels.chat.WChat;
import com.mitsugaru.worldchannels.chat.Field;
import com.mitsugaru.worldchannels.config.ConfigHandler;
import com.mitsugaru.worldchannels.config.WorldConfig;
import com.mitsugaru.worldchannels.permissions.PermissionNode;

public class WChatListener implements Listener {
    private WorldChannels plugin;
    private ConfigHandler configHandler;

    public WChatListener(WorldChannels plugin) {
        this.plugin = plugin;
        this.configHandler = plugin.getModuleForClass(ConfigHandler.class);
    }

    /**
     * Handle hashtag quick message events
     * 
     * @param event
     *            - AsyncPlayerChatEvent that occurred
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void hashMessage(final AsyncPlayerChatEvent event) {
        if(event.getPlayer() == null || event.getMessage() == null
                || !configHandler.hashQuickMessage || event.isCancelled()) {
            return;
        } else if(event.getPlayer().getWorld() == null) {
            return;
        }
        if(event.getMessage().charAt(0) != '#') {
            return;
        }
        // Hash message
        boolean ours = false;
        Channel target = null;
        final String userTag = event.getMessage().split(" ")[0]
                .replace("#", "");
        // Check world channels
        WorldConfig conf;
        try {
            conf = configHandler.getWorldConfig(event.getPlayer().getWorld()
                    .getName());
        } catch(IllegalArgumentException e) {
            plugin.getLogger().log(Level.WARNING, e.getMessage(), e);
            return;
        }
        for(Channel channel : conf.getChannels()) {
            if(channel.getTag().equalsIgnoreCase(userTag)) {
                ours = true;
                target = channel;
            }
        }
        if(ours) {
            event.setMessage(event.getMessage().replace(
                    event.getMessage().split(" ")[0], ""));
            // Handle text to channel recepients
            handleChatEvent(event, conf, target);
        }
    }

    /**
     * Listen for ChatEvents to change who listens to it. Set to HIGHEST
     * priority so that other chat plugins can do what they need to do to the
     * message/format.
     * 
     * @param event
     *            - AsyncPlayerChatEvent that occurred
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void chatEvent(final AsyncPlayerChatEvent event) {
        // Don't care about event if it is cancelled
        if(event.isCancelled() || event.getPlayer() == null) {
            return;
        }
        if(event.getMessage().charAt(0) == '#') {
            // Ignore, as we handled it earlier
            return;
        }
        // Grab player
        final Player player = event.getPlayer();
        if(event.getPlayer().getWorld() == null) {
            return;
        }
        // Get world name
        final String worldName = event.getPlayer().getWorld().getName();
        // Grab world specific config
        final WorldConfig config = configHandler.getWorldConfig(worldName);

        Channel channel = null;
        ChannelManager manager = plugin.getModuleForClass(ChannelManager.class);
        if(manager.getCurrentChannelId(player.getName()) != null) {
            channel = manager.getChannel(manager.getCurrentChannelId(player
                    .getName()));
        }
        if(channel == null) {
            // Grab default of the world
            channel = config.getDefaultChannel();
        }

        handleChatEvent(event, config, channel);
    }

    private void handleChatEvent(final AsyncPlayerChatEvent event,
            WorldConfig config, Channel channel) {
        boolean debug = configHandler.debugChat;

        // Grab player
        final Player player = event.getPlayer();

        if(debug) {
            plugin.getLogger().info(
                    player.getName() + " chat in "
                            + player.getWorld().getName() + " for channel "
                            + channel.getName() + ": " + event.getMessage());
        }

        // Check mute
        if(channel.getMuted().contains(player.getName())) {
            player.sendMessage("You are muted for channel '"
                    + channel.getName() + "' in world '"
                    + config.getWorldName() + "'");
            event.setCancelled(true);
            if(debug) {
                plugin.getLogger().info(
                        player.getName() + " muted in " + config.getWorldName()
                                + ":" + channel.getName() + " with message "
                                + event.getMessage());
            }
            return;
        }
        // Get world name
        final String worldName = event.getPlayer().getWorld().getName();
        Set<Player> receivers = new HashSet<Player>();
        if(channel.includeWorldPlayers()) {
            // Add people of the original world
            receivers.addAll(event.getPlayer().getWorld().getPlayers());
        }
        // Add all listeners from each linked channel
        for(Channel linked : channel.getRecievingChannels()) {
            for(String name : linked.getListeners()) {
                final Player linkedReceiver = plugin.getServer()
                        .getPlayer(name);
                if(linkedReceiver != null) {
                    receivers.add(linkedReceiver);
                }
            }
        }
        // Check if we're going to use local
        if(channel.isLocal()) {
            final List<Entity> entityList = new CopyOnWriteArrayList<Entity>();
            entityList.addAll(player.getNearbyEntities(channel.getRadius(),
                    channel.getRadius(), channel.getRadius()));
            for(Entity entity : entityList) {
                if(entity instanceof Player) {
                    receivers.add((Player) entity);
                }
            }
        }

        // Add broadcast worlds
        if(debug) {
            plugin.getLogger().info(
                    "Number of mirror worlds: " + config.getMirrored().size());
        }
        for(String mirror : config.getMirrored()) {
            World broadcast = plugin.getServer().getWorld(mirror);
            if(debug) {
                plugin.getLogger().info(
                        "Looking to mirror message to " + mirror + ": "
                                + (broadcast != null));
            }
            if(broadcast != null) {
                receivers.addAll(broadcast.getPlayers());
            }
        }

        boolean empty = false;
        if(receivers.isEmpty()) {
            empty = true;
        } else if(receivers.size() == 1 && receivers.contains(player)) {
            empty = true;
        }
        // Add player to receivers by default
        receivers.add(player);

        // Add observers
        for(String observer : channel.getObservers()) {
            final Player playerObserver = plugin.getServer()
                    .getPlayer(observer);
            if(playerObserver != null && playerObserver.isOnline()) {
                receivers.add(playerObserver);
            }
        }

        // Add global observers
        ChannelManager manager = plugin.getModuleForClass(ChannelManager.class);
        for(String observer : manager.getObservers()) {
            final Player playerObserver = plugin.getServer()
                    .getPlayer(observer);
            if(playerObserver != null && playerObserver.isOnline()) {
                receivers.add(playerObserver);
            }
        }

        if(debug) {
            plugin.getLogger().info(
                    "Added receiving players: " + receivers.size());
        }

        // Clear recipients
        event.getRecipients().clear();
        // Add our receivers
        event.getRecipients().addAll(receivers);
        if(empty) {
            if(config.useNobody()) {
                player.sendMessage(WChat.parseString(config.getNobodyMessage(),
                        null));
            } else {
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
        try {
            group = plugin.getChat().getPlayerGroups(player)[0];
        } catch(ArrayIndexOutOfBoundsException a) {
            // IGNORE
        } catch(NullPointerException npe) {
            group = "";
            if(configHandler.debugVault) {
                plugin.getLogger().warning(
                        "Vault threw NPE... Could not retrieve group name!");
            }
        }
        info.put(Field.GROUP, group);
        String prefix = "";
        try {
            prefix = plugin.getChat().getPlayerPrefix(worldName,
                    player.getName());
        } catch(NullPointerException npe) {
            prefix = "";
            if(configHandler.debugVault) {
                plugin.getLogger().warning(
                        "Vault threw NPE... Could not retrieve prefix!");
            }
        }
        info.put(Field.PREFIX, prefix);
        String suffix = "";
        try {
            suffix = plugin.getChat().getPlayerSuffix(worldName,
                    player.getName());
        } catch(NullPointerException npe) {
            suffix = "";
            if(configHandler.debugVault) {
                plugin.getLogger().warning(
                        "Vault threw NPE... Could not retrieve suffix!");
            }
        }
        info.put(Field.SUFFIX, suffix);
        info.put(Field.MESSAGE, "%2\\$s");
        // Check if we are going to edit the format at all
        if(channel.isFormat()) {
            final String format = channel.getFormatterString();
            if(!format.equals("")) {
                event.setFormat(WChat.parseString(format, info));
            }
        } else if(config.useFormatter()) {
            final String format = config.getFormat();
            if(!format.equals("")) {
                event.setFormat(WChat.parseString(format, info));
            }
        } else if(configHandler.useFormatter()) {
            final String format = configHandler.getFormat();
            if(!format.equals("")) {
                event.setFormat(WChat.parseString(format, info));
            }
        }
        // Check if we colorize their chat
        if(player.hasPermission(PermissionNode.COLORIZE.getNode())) {
            event.setMessage(WorldChannels.colorizeText(event.getMessage()));
        }
    }
}
