package com.duy.fremote.server.command;

import android.text.TextUtils;

import java.util.ArrayList;

public class CommandBuilder {
    private ArrayList<String> args = new ArrayList<>();

    public CommandBuilder() {
    }

    public CommandBuilder add(String arg) {
        args.add(arg);
        return this;
    }

    public CommandBuilder on() {
        return add(CommandConstants.ON);
    }

    public CommandBuilder off() {
        return add(CommandConstants.OFF);
    }

    public CommandBuilder pin(int pin) {
        return add(String.valueOf(pin));
    }

    public CommandBuilder request() {
        add(CommandConstants.GET);
        return this;
    }

    public CommandBuilder requestHumidity() {
        add(CommandConstants.GET_HUMIDITY);
        return this;
    }

    public CommandBuilder requestTemperature() {
        add(CommandConstants.GET_TEMPERATURE);
        return this;
    }

    public String build() {
        return TextUtils.join(" ", args);
    }

}
