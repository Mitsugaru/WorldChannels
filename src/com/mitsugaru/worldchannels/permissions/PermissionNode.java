package com.mitsugaru.worldchannels.permissions;

public enum PermissionNode
{
	ADMIN(".admin"), COLORIZE(".colorize"), OBSERVE(".observe"), OBSERVE_AUTO(
			".observe.auto"), SHOUT(".shout");
	private static final String prefix = "WorldChannels";
	private String node;

	private PermissionNode(String node)
	{
		this.node = prefix + node;
	}

	public String getNode()
	{
		return node;
	}

}
