package controller.managers;

import model.api.Manager;
import model.api.ManagerDecorator;
import model.entity.Item;
import network.api.Peer;

public class ResilienceItemManagerDecorator extends ManagerDecorator<Item>{

	public ResilienceItemManagerDecorator(Manager<Item> em, Peer peer) {
		super(em);
	}

}
