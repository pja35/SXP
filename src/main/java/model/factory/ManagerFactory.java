package model.factory;

import model.api.AsyncManager;
import model.entity.Item;
import model.manager.AsyncManagerAdapter;
import model.manager.NetworkItemManagerDecorator;
import model.manager.ResilianceItemManager;
import model.persistance.ItemManager;
import network.api.Peer;

public class ManagerFactory {
	public static AsyncManager<Item> createNetworkResilianceItemManager(Peer peer, String who) {
		AsyncManagerAdapter<Item> adapter = new AsyncManagerAdapter<Item>(new ItemManager());
		NetworkItemManagerDecorator networkD = new NetworkItemManagerDecorator(adapter, peer, who);
		ResilianceItemManager resiNetworkD = new ResilianceItemManager(networkD, peer);
		return resiNetworkD;
	}
}
