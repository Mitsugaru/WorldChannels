package com.mitsugaru.WorldChannels.permissions;

public enum Permission
{
	ADMIN(".admin");
	private static final String prefix = "Karmiconomy";
	private String node;

	private Permission(String node)
	{
		this.node = prefix + node;
	}
	
	public String getNode()
	{
		return node;
	}

}
