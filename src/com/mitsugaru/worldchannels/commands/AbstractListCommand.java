package com.mitsugaru.worldchannels.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mitsugaru.worldchannels.WorldChannels;
import com.mitsugaru.worldchannels.chat.Channel;
import com.mitsugaru.worldchannels.config.ConfigHandler;
import com.mitsugaru.worldchannels.config.localize.Flag;
import com.mitsugaru.worldchannels.config.localize.LocalString;
import com.mitsugaru.worldchannels.config.localize.Localizer;
import com.mitsugaru.worldchannels.services.ICommand;

public abstract class AbstractListCommand implements ICommand {

    protected final static int LIST_LIMIT = 8;
    protected static final Map<String, Integer> currentPage = new HashMap<String, Integer>();
    protected static final Map<String, String> pageWorld = new HashMap<String, String>();
    
    protected void listChannels(CommandSender sender, String world,
            int pageAdjust, Map<Flag, String> info, ConfigHandler configHandler) {
        
        List<Channel> hold = new ArrayList<Channel>();
        hold.addAll(configHandler.getGlobalChannels());
        try {
            hold.addAll(configHandler.getWorldConfig(world).getChannels());
        } catch(IllegalArgumentException e) {
            // Ignore
            info.put(Flag.EXTRA, world);
            info.put(Flag.REASON, "world");
            sender.sendMessage(Localizer.parseString(LocalString.UNKNOWN, info));
        }
        if(hold.isEmpty()) {
            // notify player that there are no available channels... somehow.
            sender.sendMessage(ChatColor.YELLOW + WorldChannels.TAG
                    + " No channels...");
            return;
        }
        Channel[] list = hold.toArray(new Channel[0]);
        
        //Adjust page.
        if(!currentPage.containsKey(sender.getName())) {
            currentPage.put(sender.getName(), 0);
        }
        int page = currentPage.get(sender.getName());
        if(pageAdjust != 0) {
            page += pageAdjust;
        }
        if(page < 0) {
            page = 0;
            currentPage.put(sender.getName(), 0);
        } else if(page > (list.length / LIST_LIMIT)) {
            page -= 1;
            currentPage.put(sender.getName(), page);
        }
        
        for(int i = (currentPage.get(sender.getName()).intValue() * LIST_LIMIT); i < (currentPage
                .get(sender.getName()).intValue() * LIST_LIMIT + LIST_LIMIT); i++) {
            if(i < list.length) {
                if(list[i].getListeners().contains(sender.getName())) {
                    // They are a listener
                    sender.sendMessage(ChatColor.GREEN + "#" + list[i].getTag()
                            + " | " + list[i].getName());
                } else if(list[i].getObservers().contains(sender.getName())) {
                    // They are an observer
                    sender.sendMessage(ChatColor.GOLD + "#" + list[i].getTag()
                            + " | " + list[i].getName());
                } else if(list[i].getMuted().contains(sender.getName())) {
                    // They are muted
                    sender.sendMessage(ChatColor.RED + "#" + list[i].getTag()
                            + " | " + list[i].getName());
                } else {
                    // They are not a part of the channel
                    sender.sendMessage(ChatColor.GRAY + "#" + list[i].getTag()
                            + " | " + list[i].getName());
                }
            } else {
                break;
            }
        }
    }
}
