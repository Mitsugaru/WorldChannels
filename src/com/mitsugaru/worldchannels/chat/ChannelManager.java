package com.mitsugaru.worldchannels.chat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.services.WCModule;

public class ChannelManager extends WCModule {
    
    private final Map<String, Channel> registeredChannels = new HashMap<String, Channel>();
    private final Map<String, String> currentChannel = new ConcurrentHashMap<String, String>();
    private final Set<String> observers = new HashSet<String>();

    public ChannelManager(WorldChannels plugin) {
        super(plugin);
    }
    
    public void registerChannel(String id, Channel channel) {
        registeredChannels.put(id, channel);
    }
    
    public void unregisterChannel(String id) {
        registeredChannels.remove(id);
    }
    
    public void clearChannels() {
        registeredChannels.clear();
        currentChannel.clear();
    }
    
    public Channel getChannel(String id) {
        return registeredChannels.get(id);
    }
    
    public String getCurrentChannelId(String playerName) {
        return currentChannel.get(playerName);
    }
    
    public void setCurrentChannel(String playerName, String channelId) {
        currentChannel.put(playerName, channelId);
    }
    
    public void removeCurrentChannel(String playerName) {
        currentChannel.remove(playerName);
    }
    
    public Set<String> getObservers() {
        return observers;
    }

    @Override
    public void starting() {
    }

    @Override
    public void closing() {
    }

}
