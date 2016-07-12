package network.impl;

import java.util.HashMap;

import network.api.Messages;

public class MessagesGeneric implements Messages{

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
	public void setWho(String who) {
		addField("WHO", who);
	}

	@Override
	public String getWho() {
		return getMessage("WHO");
	}
	
	public void addField(String name, String value) {
		fields.put(name, value);
	}

}
