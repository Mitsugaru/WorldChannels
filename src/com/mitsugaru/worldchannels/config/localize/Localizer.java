package com.mitsugaru.worldchannels.config.localize;

import java.util.EnumMap;
import java.util.Map.Entry;

import com.mitsugaru.worldchannels.WorldChannels;

public class Localizer {
    
    private static LocalizeConfig config;
    
    public Localizer(WorldChannels plugin) {
        config = plugin.getModuleForClass(LocalizeConfig.class);
    }

    public static String parseString(LocalString target, EnumMap<Flag, String> replace) {
        String out = WorldChannels.colorizeText(config.getMessage(target));
        if(replace != null) {
            for(Entry<Flag, String> entry : replace.entrySet()) {
                out = out
                        .replaceAll(entry.getKey().getFlag(), entry.getValue());
            }
        }
        return out;
    }
}
