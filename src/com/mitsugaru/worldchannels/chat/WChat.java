package com.mitsugaru.worldchannels.chat;

import java.util.EnumMap;
import java.util.Map.Entry;

import com.mitsugaru.worldchannels.WorldChannels;

public class WChat {
    private String string;

    private WChat(String s) {
        this.string = s;
    }

    public static String parseString(String s, EnumMap<Field, String> replace) {
        String out = s;
        if(replace != null) {
            for(Entry<Field, String> entry : replace.entrySet()) {
                out = out.replaceAll(entry.getKey().getField(),
                        entry.getValue());
            }
        }
        return WorldChannels.colorizeText(out);
    }

    public String parseString(EnumMap<Field, String> replace) {
        String out = WorldChannels.colorizeText(string);
        if(replace != null) {
            for(Entry<Field, String> entry : replace.entrySet()) {
                out = out.replaceAll(entry.getKey().getField(),
                        entry.getValue());
            }
        }
        return out;
    }
    
}
