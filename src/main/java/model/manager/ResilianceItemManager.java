package model.manager;

import model.api.AsyncManager;
import model.api.AsyncManagerDecorator;
import model.entity.Item;
import network.api.Peer;

public class ResilianceItemManager extends AsyncManagerDecorator<Item>{

	public ResilianceItemManager(AsyncManager<Item> em, Peer peer) {
		super(em);
	}

}
