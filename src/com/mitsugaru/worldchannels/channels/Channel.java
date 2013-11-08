package com.mitsugaru.worldchannels.channels;

import java.util.HashSet;
import java.util.Set;

public class Channel {

    private String tag = "", name = "", world = "", formatterString = "",
            nobodyString = "", permissionJoin = "", permissionLeave = "",
            permissionKick = "", permissionMute = "";
    private Set<String> muted = new HashSet<String>();
    private Set<String> listeners = new HashSet<String>();
    private Set<String> observers = new HashSet<String>();
    private Set<Channel> channels = new HashSet<Channel>();
    private boolean local = false, global = false, includeWorldPlayers = false,
            auto = false, format = false, nobody = false;
    private int radius = 100;

    public Channel(String tag, String name, String world) {
        this.tag = tag;
        this.name = name;
        this.world = world;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Set<String> getListeners() {
        return listeners;
    }

    public void addListener(String name) {
        listeners.add(name);
    }

    public void removeListener(String name) {
        listeners.remove(name);
    }

    public Set<String> getObservers() {
        return observers;
    }

    public void addObserver(String name) {
        observers.add(name);
    }

    public void removeObserver(String name) {
        observers.remove(name);
    }

    public Set<String> getMuted() {
        return muted;
    }

    public void addMutedPlayer(String name) {
        muted.add(name);
    }

    public void removeMutedPlayer(String name) {
        muted.remove(name);
    }

    public Set<Channel> getRecievingChannels() {
        return channels;
    }

    public void addChannel(Channel channel) {
        channels.add(channel);
    }

    public void removeChannel(Channel channel) {
        channels.remove(channel);
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public boolean includeWorldPlayers() {
        return includeWorldPlayers;
    }

    public void setIncludeWorldPlayers(boolean include) {
        this.includeWorldPlayers = include;
    }

    public boolean isAutoJoin() {
        return auto;
    }

    public void setAutoJoin(boolean auto) {
        this.auto = auto;
    }

    public String getFormatterString() {
        return formatterString;
    }

    public void setFormatterString(String formatterString) {
        this.formatterString = formatterString;
    }

    public String getNobodyString() {
        return nobodyString;
    }

    public void setNobodyString(String nobodyString) {
        this.nobodyString = nobodyString;
    }

    public boolean isFormat() {
        return format;
    }

    public void setFormat(boolean format) {
        this.format = format;
    }

    public boolean isNobody() {
        return nobody;
    }

    public void setNobody(boolean nobody) {
        this.nobody = nobody;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getPermissionJoin() {
        return permissionJoin;
    }

    public void setPermissionJoin(String permissionJoin) {
        this.permissionJoin = permissionJoin;
    }

    public String getPermissionLeave() {
        return permissionLeave;
    }

    public void setPermissionLeave(String permissionLeave) {
        this.permissionLeave = permissionLeave;
    }

    public String getPermissionKick() {
        return permissionKick;
    }

    public void setPermissionKick(String permissionKick) {
        this.permissionKick = permissionKick;
    }

    public String getPermissionMute() {
        return permissionMute;
    }

    public void setPermissionMute(String permissionMute) {
        this.permissionMute = permissionMute;
    }
}
