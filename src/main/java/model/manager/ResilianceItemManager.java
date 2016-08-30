package model.manager;

import model.api.Manager;
import model.api.ManagerDecorator;
import model.entity.Item;
import network.api.Peer;

public class ResilianceItemManager extends ManagerDecorator<Item>{

	public ResilianceItemManager(Manager<Item> em, Peer peer) {
		super(em);
	}

}
