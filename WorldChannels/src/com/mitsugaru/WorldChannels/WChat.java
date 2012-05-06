package com.mitsugaru.WorldChannels;

import java.util.EnumMap;
import java.util.Map.Entry;

public class WChat
{
	private String string;

	private WChat(String s)
	{
		this.string = s;
	}
	
	public static String parseString(String s, EnumMap<Flag, String> replace)
	{
		String out = WorldChannels.colorizeText(s);
		if (replace != null)
		{
			for (Entry<Flag, String> entry : replace.entrySet())
			{
				out = out.replaceAll(entry.getKey().getFlag(), entry.getValue());
			}
		}
		return out;
	}

	public String parseString(EnumMap<Flag, String> replace)
	{
		String out = WorldChannels.colorizeText(string);
		if (replace != null)
		{
			for (Entry<Flag, String> entry : replace.entrySet())
			{
				out = out.replaceAll(entry.getKey().getFlag(), entry.getValue());
			}
		}
		return out;
	}

	public enum Flag
	{
		NAME("%name"), WORLD("%world"), PREFIX("%prefix"), SUFFIX("%suffix"), MESSAGE(
				"%message");

		private String flag;

		private Flag(String flag)
		{
			this.flag = flag;
		}

		public String getFlag()
		{
			return flag;
		}
	}
}
