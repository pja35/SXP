package model.factory;

import controller.managers.CryptoItemManagerDecorator;
import controller.managers.NetworkContractManagerDecorator;
import controller.managers.NetworkItemManagerDecorator;
import controller.managers.ResilienceContractManagerDecorator;
import controller.managers.ResilienceItemManagerDecorator;
import model.api.Manager;
import model.entity.ContractEntity;
import model.entity.Item;
import model.entity.User;
import model.manager.ManagerAdapter;
import model.syncManager.ContractSyncManagerImpl;
import model.syncManager.ItemSyncManagerImpl;
import network.api.Peer;

public class ManagerFactory {
	
	public static Manager<Item> createNetworkResilianceItemManager(Peer peer, String who) {
		
		ManagerAdapter<Item> adapter = new ManagerAdapter<Item>(new ItemSyncManagerImpl());
		
		NetworkItemManagerDecorator networkD = new NetworkItemManagerDecorator(adapter, peer, who);
		
		ResilienceItemManagerDecorator resiNetworkD = new ResilienceItemManagerDecorator(networkD, peer);
		
		return resiNetworkD;
	}
	
	
	public static Manager<Item> createCryptoNetworkResilianceItemManager(Peer peer, String who,User user) {
		
		ManagerAdapter<Item> adapter = new ManagerAdapter<Item>(new ItemSyncManagerImpl());
		
		NetworkItemManagerDecorator networkD = new NetworkItemManagerDecorator(adapter, peer, who);
		
		CryptoItemManagerDecorator cyptoItemDecorator = new CryptoItemManagerDecorator(networkD, user);
		
		ResilienceItemManagerDecorator resiNetworkD = new ResilienceItemManagerDecorator(cyptoItemDecorator, peer);
		
		return resiNetworkD;
	}
	
	
	
	
	
	
	
	public static Manager<ContractEntity> createNetworkResilianceContractManager(Peer peer, String who) {
		ManagerAdapter<ContractEntity> adapter = new ManagerAdapter<ContractEntity>(new ContractSyncManagerImpl());
		NetworkContractManagerDecorator networkD = new NetworkContractManagerDecorator (adapter, peer, who);
		ResilienceContractManagerDecorator resiNetworkD = new ResilienceContractManagerDecorator(networkD, peer);
		return resiNetworkD;
	}
}
