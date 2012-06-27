package com.mitsugaru.WorldChannels;

import java.util.EnumMap;
import java.util.Map.Entry;

import com.mitsugaru.WorldChannels.config.LocalizeConfig;

public enum LocalString {
    PERMISSION_DENY(LocalizeConfig.permissionDeny), UNKNOWN_COMMAND(
	    LocalizeConfig.unknownCommand), RELOAD_CONFIG(
	    LocalizeConfig.reloadConfig), HELP_HELP(LocalizeConfig.helpHelp), HELP_VERSION(
	    LocalizeConfig.helpVersion), HELP_ADMIN_RELOAD(
	    LocalizeConfig.helpAdminReload), HELP_SHOUT(
	    LocalizeConfig.helpShout), OBSERVER_ON(LocalizeConfig.observerOn), OBSERVER_OFF(
	    LocalizeConfig.observerOff);

    private String string;

    private LocalString(String s) {
	this.string = s;
    }

    public String parseString(EnumMap<Flag, String> replace) {
	String out = WorldChannels.colorizeText(string);
	if (replace != null) {
	    for (Entry<Flag, String> entry : replace.entrySet()) {
		out = out
			.replaceAll(entry.getKey().getFlag(), entry.getValue());
	    }
	}
	return out;
    }

    public enum Flag {
	NAME("%name"), EVENT("%event"), REASON("%reason"), EXTRA("%extra"), TAG(
		"%tag"), AMOUNT("%amount");

	private String flag;

	private Flag(String flag) {
	    this.flag = flag;
	}

	public String getFlag() {
	    return flag;
	}
    }
}
