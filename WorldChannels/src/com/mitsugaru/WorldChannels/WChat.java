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
	
	public static String parseString(String s, EnumMap<Field, String> replace)
	{
		String out = WorldChannels.colorizeText(s);
		if (replace != null)
		{
			for (Entry<Field, String> entry : replace.entrySet())
			{
				out = out.replaceAll(entry.getKey().getField(), entry.getValue());
			}
		}
		return out;
	}

	public String parseString(EnumMap<Field, String> replace)
	{
		String out = WorldChannels.colorizeText(string);
		if (replace != null)
		{
			for (Entry<Field, String> entry : replace.entrySet())
			{
				out = out.replaceAll(entry.getKey().getField(), entry.getValue());
			}
		}
		return out;
	}

	public enum Field
	{
		NAME("%name"), WORLD("%world"), PREFIX("%prefix"), SUFFIX("%suffix"), MESSAGE(
				"%message");

		private String field;

		private Field(String field)
		{
			this.field = field;
		}

		public String getField()
		{
			return field;
		}
	}
}
