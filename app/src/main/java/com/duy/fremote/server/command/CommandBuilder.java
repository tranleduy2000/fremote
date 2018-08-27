package com.duy.fremote.server.command;

import java.util.ArrayList;
import java.util.Iterator;

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
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = args.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(" ");
                sb.append(it.next());
            }
        }
        return sb.toString();
    }

}
