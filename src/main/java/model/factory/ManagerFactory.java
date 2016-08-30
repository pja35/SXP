package model.factory;

import model.api.Manager;
import model.entity.Item;
import model.manager.AsyncManagerAdapter;
import model.manager.NetworkItemManagerDecorator;
import model.manager.ResilianceItemManager;
import model.syncManager.ItemSyncManagerImpl;
import network.api.Peer;

public class ManagerFactory {
	public static Manager<Item> createNetworkResilianceItemManager(Peer peer, String who) {
		AsyncManagerAdapter<Item> adapter = new AsyncManagerAdapter<Item>(new ItemSyncManagerImpl());
		NetworkItemManagerDecorator networkD = new NetworkItemManagerDecorator(adapter, peer, who);
		ResilianceItemManager resiNetworkD = new ResilianceItemManager(networkD, peer);
		return resiNetworkD;
	}
}
