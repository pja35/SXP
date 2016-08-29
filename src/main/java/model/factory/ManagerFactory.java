package model.factory;

import model.api.AsyncManager;
import model.entity.Item;
import model.manager.ItemManager;
import model.managerDecorator.AsyncManagerAdapter;
import model.managerDecorator.NetworkItemManagerDecorator;
import model.managerDecorator.ResilianceItemManager;
import network.api.Peer;

public class ManagerFactory {
	public static AsyncManager<Item> createNetworkResilianceItemManager(Peer peer, String who) {
		AsyncManagerAdapter<Item> adapter = new AsyncManagerAdapter<Item>(new ItemManager());
		NetworkItemManagerDecorator networkD = new NetworkItemManagerDecorator(adapter, peer, who);
		ResilianceItemManager resiNetworkD = new ResilianceItemManager(networkD, peer);
		return resiNetworkD;
	}
}
