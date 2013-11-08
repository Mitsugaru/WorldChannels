package com.mitsugaru.worldchannels.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.chat.Channel;
import com.mitsugaru.worldchannels.config.ConfigHandler;
import com.mitsugaru.worldchannels.config.WorldConfig;

public class PlayerChangedWorldTask implements Runnable {

    private WorldChannels plugin;
    private Map<String, String> currentWorld = new HashMap<String, String>();

    public PlayerChangedWorldTask(WorldChannels plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        final Player[] players = plugin.getServer().getOnlinePlayers();
        final Map<String, String> changed = new HashMap<String, String>();
        for(Player player : players) {
            final String world = currentWorld.get(player.getName());
            if(world != null
                    && !world.equalsIgnoreCase(player.getWorld().getName())) {
                // Different world
                changed.put(player.getName(), world);
            } else if(world == null) {
                // unknown player, add to map
                changed.put(player.getName(), world);
            }
        }
        if(changed.isEmpty()) {
            return;
        }
        synchronized (WorldChannels.currentChannel) {
            for(Map.Entry<String, String> entry : changed.entrySet()) {
                // Remove player as listener from all channels
                final List<World> worlds = plugin.getServer().getWorlds();
                for(World world : worlds) {
                    final WorldConfig conf = plugin.getModuleForClass(
                            ConfigHandler.class)
                            .getWorldConfig(world.getName());
                    if(conf != null) {
                        for(Channel channel : conf.getChannels()) {
                            channel.removeListener(entry.getKey());
                        }
                    }
                }
                // Grab world config
                WorldConfig conf;
                if(entry.getValue() == null) {
                    conf = plugin.getModuleForClass(ConfigHandler.class)
                            .getWorldConfig(
                                    plugin.getServer()
                                            .getPlayer(entry.getKey())
                                            .getWorld().getName());
                } else {
                    conf = plugin.getModuleForClass(ConfigHandler.class)
                            .getWorldConfig(entry.getValue());
                }

                // Grab all autojoin channels of world and add them as a
                // listener
                for(Channel channel : conf.getChannels()) {
                    if(channel.isAutoJoin()) {
                        channel.addListener(entry.getKey());
                    }
                }
                // Grab default channel
                final Channel channel = conf.getDefaultChannel();
                // Set player to default channel
                WorldChannels.currentChannel.put(entry.getKey(), channel);
                // Set current world
                currentWorld.put(entry.getKey(), entry.getValue());
            }
        }
    }

}
