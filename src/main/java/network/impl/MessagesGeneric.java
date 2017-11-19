package network.impl;

import network.api.Messages;

import java.util.HashMap;

public class MessagesGeneric implements Messages {

    private HashMap<String, String> fields = new HashMap<>();


    @Override
    public String getMessage(String name) {
        return fields.get(name);
    }

    @Override
    public String[] getNames() {
        return fields.keySet().toArray(new String[1]);
    }

    @Override
    public String getWho() {
        return getMessage("WHO");
    }

    @Override
    public void setWho(String who) {
        addField("WHO", who);
    }

    public void addField(String name, String value) {
        fields.put(name, value);
    }

}
