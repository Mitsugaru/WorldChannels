package com.mitsugaru.worldchannels.tasks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.mitsugaru.worldchannels.WorldChannels;

public class WorldAnnouncerTask implements Runnable
{

	private List<String> announcements = new ArrayList<String>();
	private String worldName = "world";
	private int current = 0;
	
	public WorldAnnouncerTask(String world, List<String> announcements)
	{
		this.announcements = announcements;
		this.worldName = world;
	}
	
	@Override
	public void run()
	{
		final World world = Bukkit.getServer().getWorld(worldName);
		if(world == null)
		{
			return;
		}
		final String out = WorldChannels.colorizeText(announcements.get(current++));
		if(current >= announcements.size())
		{
			current = 0;
		}
		for(Player player : world.getPlayers())
		{
			player.sendMessage(out);
		}
	}

}
