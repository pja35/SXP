package network.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;

import network.api.Messages;
import network.api.annotation.MessageElement;

public class MessagesImpl implements Messages{

	private String who;
	
	@Override
	public String getMessage(String name) {
		if(name.equals("WHO")) return who;
		for(Field f : this.getClass().getDeclaredFields()) {
			f.setAccessible(true);
			MessageElement m = f.getAnnotation(MessageElement.class);
			if(m != null && m.value().equals(name)) {
				try {
					return (String) f.get(this);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			f.setAccessible(false);
		}
		throw new RuntimeException("field doesn't exist !");
	}

	@Override
	public String[] getNames() {
		ArrayList<String> names = new ArrayList<>();
		
		for(Field f : this.getClass().getDeclaredFields()) {
			f.setAccessible(true);
			MessageElement m = f.getAnnotation(MessageElement.class);
			if(m != null) {
				names.add(m.value());
			}
			f.setAccessible(false);
		}
		names.add("WHO");
		return names.toArray(new String[1]);
	}

	@Override
	public void setWho(String who) {
		this.who = who;
	}
	
	public String getWho() {
		return who;
	}

}
