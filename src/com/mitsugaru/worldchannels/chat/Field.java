package com.mitsugaru.worldchannels.chat;

public enum Field {
    NAME("%name"),
    WORLD("%world"),
    PREFIX("%prefix"),
    SUFFIX("%suffix"),
    MESSAGE("%message"),
    GROUP("%group");

    private String field;

    private Field(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
