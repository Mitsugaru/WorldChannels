package com.mitsugaru.worldchannels;

import java.util.EnumMap;
import java.util.Map.Entry;

import com.mitsugaru.worldchannels.config.LocalizeConfig;

public enum LocalString {
    PERMISSION_DENY(LocalizeConfig.permissionDeny),
    UNKNOWN(LocalizeConfig.unknown),
    RELOAD_CONFIG(LocalizeConfig.reloadConfig),
    HELP_HELP(LocalizeConfig.helpHelp),
    HELP_VERSION(LocalizeConfig.helpVersion),
    HELP_ADMIN_RELOAD(LocalizeConfig.helpAdminReload),
    HELP_SHOUT(LocalizeConfig.helpShout),
    HELP_OBSERVE(LocalizeConfig.helpObserve),
    OBSERVER_ON(LocalizeConfig.observerOn),
    OBSERVER_OFF(LocalizeConfig.observerOff),
    NO_CONSOLE(LocalizeConfig.noConsole),
    MISSING_PARAM(LocalizeConfig.missingParam);

    private String string;

    private LocalString(String s) {
        this.string = s;
    }

    public String parseString(EnumMap<Flag, String> replace) {
        String out = WorldChannels.colorizeText(string);
        if(replace != null) {
            for(Entry<Flag, String> entry : replace.entrySet()) {
                out = out
                        .replaceAll(entry.getKey().getFlag(), entry.getValue());
            }
        }
        return out;
    }

    public enum Flag {
        NAME("%name"),
        EVENT("%event"),
        REASON("%reason"),
        EXTRA("%extra"),
        TAG("%tag");

        private String flag;

        private Flag(String flag) {
            this.flag = flag;
        }

        public String getFlag() {
            return flag;
        }
    }
}
