package com.mitsugaru.WorldChannels.events;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import com.mitsugaru.WorldChannels.WChat;
import com.mitsugaru.WorldChannels.WChat.Field;
import com.mitsugaru.WorldChannels.WorldChannels;
import com.mitsugaru.WorldChannels.config.WorldConfig;
import com.mitsugaru.WorldChannels.config.ConfigHandler;
import com.mitsugaru.WorldChannels.permissions.PermissionHandler;
import com.mitsugaru.WorldChannels.permissions.PermissionNode;

public class WChatListener implements Listener {
    private WorldChannels plugin;
    private ConfigHandler configHandler;

    public WChatListener(WorldChannels plugin) {
	this.plugin = plugin;
	this.configHandler = plugin.getConfigHandler();
    }

    /**
     * Listen for ChatEvents to change who listens to it. Set to HIGHEST
     * priority so that other chat plugins can do what they need to do to the
     * message/format.
     * 
     * @param PlayerChatEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void chatEvent(final PlayerChatEvent event) {
	// Don't care about event if it is cancelled
	if (event.isCancelled() || event.getPlayer() == null) {
	    return;
	}
	// Grab player
	final Player player = event.getPlayer();
	if (event.getPlayer().getWorld() == null) {
	    return;
	}
	// Get world name
	final String worldName = event.getPlayer().getWorld().getName();
	// Grab world specific config
	final WorldConfig config = plugin.getConfigHandler().getWorldConfig(
		worldName);
	
	Set<Player> receivers = null;
	if (config.includeLocalPlayers()) {
	    // Add people of the original world
	    receivers = new HashSet<Player>(event.getPlayer().getWorld()
		    .getPlayers());
	} else {
	    receivers = new HashSet<Player>();
	}
	// Grab list
	final Set<String> worldList = plugin.getConfigHandler()
		.getWorldChannels(worldName);
	// Check if empty. If empty, we don't add any other world checks
	if (!worldList.isEmpty()) {
	    for (String name : worldList) {
		final World world = plugin.getServer().getWorld(name);
		if (world == null) {
		    continue;
		}
		receivers.addAll(world.getPlayers());
	    }
	}
	// Check if we're going to use local
	if (config.useLocal()) {
	    final List<Entity> entities = player.getNearbyEntities(
		    config.getLocalRadius(), config.getLocalRadius(),
		    config.getLocalRadius());
	    for (Entity entity : entities) {
		if (entity instanceof Player) {
		    receivers.add((Player) entity);
		}
	    }
	}
	boolean empty = false;
	if(receivers.isEmpty())
	{
	    empty = true;
	    if(config.useNobody())
	    {
		player.sendMessage(WChat.parseString(config.getNobodyMessage(), null));
	    }
	    else
	    {
		player.sendMessage(WChat.parseString(configHandler.getNobodyMessage(), null));
	    }
	}
	//Add player to receivers by default
	receivers.add(player);
	// Add observers
	for (String observer : WorldChannels.observers) {
	    final Player playerObserver = plugin.getServer()
		    .getPlayer(observer);
	    if (playerObserver != null && playerObserver.isOnline()) {
		receivers.add(playerObserver);
	    }
	}
	// Clear recipients
	event.getRecipients().clear();
	// Add our receivers
	event.getRecipients().addAll(receivers);
	if(empty)
	{
	    return;
	}
	// Set info of fields for formatting message and format
	final EnumMap<Field, String> info = new EnumMap<Field, String>(
		Field.class);
	info.put(Field.NAME, "%1\\$s");
	info.put(Field.WORLD, worldName);
	try {
	    info.put(Field.GROUP,
		    plugin.getChat().getPlayerGroups(player)[0]);
	} catch (ArrayIndexOutOfBoundsException a) {
	    // IGNORE
	}
	info.put(
		Field.PREFIX,
		plugin.getChat().getPlayerPrefix(worldName,
			player.getName()));
	info.put(
		Field.SUFFIX,
		plugin.getChat().getPlayerSuffix(worldName,
			player.getName()));
	info.put(Field.MESSAGE, "%2\\$s");
	// Check if we are going to edit the format at all
	if (config.useFormatter()) {
	    final String format = config.getFormat();
	    if (!format.equals("")) {
		event.setFormat(WChat.parseString(format, info));
	    }
	} else if (configHandler.useFormatter()) {
	    final String format = configHandler.getFormat();
	    if (!format.equals("")) {
		event.setFormat(WChat.parseString(format, info));
	    }
	}
	// Check if we colorize their chat
	if (PermissionHandler.checkPermission(player,
		PermissionNode.COLORIZE.getNode())) {
	    event.setMessage(WorldChannels.colorizeText(event.getMessage()));
	}
    }
}
