package com.mitsugaru.worldchannels.config.localize;

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
