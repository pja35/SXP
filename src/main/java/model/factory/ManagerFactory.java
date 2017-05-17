package model.factory;

import controller.managers.CryptoItemManagerDecorator;
import controller.managers.CryptoMessageManagerDecorator;
import controller.managers.CryptoUserManagerDecorator;
import controller.managers.NetworkContractManagerDecorator;
import controller.managers.NetworkItemManagerDecorator;
import controller.managers.NetworkMessageManagerDecorator;
import controller.managers.NetworkUserManagerDecorator;
import controller.managers.ResilienceContractManagerDecorator;
import controller.managers.ResilienceItemManagerDecorator;
import model.api.ItemSyncManager;
import model.api.Manager;
import model.api.UserSyncManager;
import model.entity.ContractEntity;
import model.entity.Item;
import model.entity.Message;
import model.entity.User;
import model.manager.ManagerAdapter;
import model.syncManager.ContractSyncManagerImpl;
import model.syncManager.ItemSyncManagerImpl;
import model.syncManager.MessageSyncManagerImpl;
import model.syncManager.UserSyncManagerImpl;
import network.api.Peer;

public class ManagerFactory {
	
	
	/* ============================= ITEM  ============================= */
	
	//TODO : to be deleted
	//old version, the new version is createCryptoNetworkResilianceItemManager(...)
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
	
	public static Manager<Item> createCryptoNetworkResilianceItemManager(ItemSyncManager em,Peer peer, String who,User user) {		
		ManagerAdapter<Item> adapter = new ManagerAdapter<Item>(em);	
		NetworkItemManagerDecorator networkD = new NetworkItemManagerDecorator(adapter, peer, who);		
		CryptoItemManagerDecorator cyptoItemDecorator = new CryptoItemManagerDecorator(networkD, user);		
		ResilienceItemManagerDecorator resiNetworkD = new ResilienceItemManagerDecorator(cyptoItemDecorator, peer);
		return resiNetworkD;
	}
	
	/* ============================= USER  ============================= */  
	
	public static Manager<User> createCryptoUserManager(User user) {
		ManagerAdapter<User> adapter = new ManagerAdapter<User>(new UserSyncManagerImpl());
		CryptoUserManagerDecorator hasherDecoratorUser = new CryptoUserManagerDecorator(adapter,user);
		return hasherDecoratorUser;
	}
	
	public static Manager<User> createCryptoUserManager(UserSyncManager em,User user) {
		CryptoUserManagerDecorator hasherDecoratorUser = new CryptoUserManagerDecorator(new ManagerAdapter<User>(em),user);
		return hasherDecoratorUser;
	}
	
	public static Manager<User> createCryptoNetworkUserManager(Peer peer, String who,User user) {
		ManagerAdapter<User> adapter = new ManagerAdapter<User>(new UserSyncManagerImpl());
		NetworkUserManagerDecorator networkD = new NetworkUserManagerDecorator(adapter, peer, who);
		CryptoUserManagerDecorator cyptoDecorator = new CryptoUserManagerDecorator(networkD, user);
		return cyptoDecorator;
	}
	
	public static Manager<User> createNetworkResilianceUserManager(Peer peer, String who){
		ManagerAdapter<User> adapter = new ManagerAdapter<User>(new UserSyncManagerImpl());
		NetworkUserManagerDecorator networkD = new NetworkUserManagerDecorator(adapter, peer, who);
		//TODO : resilience
		//ResilienceUserManagerDecorator resiNetworkD = new ResilienceUserManagerDecorator(networkD, peer);
		return networkD;
	}
	
	/* ============================= MESSAGE  ============================= */
	
	public static Manager<Message> createCryptoMessageManager(User receiver,User sender) {
		
		ManagerAdapter<Message> adapter = new ManagerAdapter<Message>(new MessageSyncManagerImpl());
		CryptoMessageManagerDecorator messageManagerDecorator = new CryptoMessageManagerDecorator(adapter,null,receiver,sender);
		
		return messageManagerDecorator;
	}
	
	public static Manager<Message> createNetworkResilianceMessageManager(Peer peer, String who,User receiver,User sender){
		ManagerAdapter<Message> adapter = new ManagerAdapter<Message>(new MessageSyncManagerImpl());
		NetworkMessageManagerDecorator networkD = new NetworkMessageManagerDecorator(adapter, peer, who);
		CryptoMessageManagerDecorator messageManagerDecorator = new CryptoMessageManagerDecorator(networkD,who,receiver,sender);
		//TODO : resilience
		//ResilienceUserManagerDecorator resiNetworkD = new ResilienceUserManagerDecorator(networkD, peer);
		return messageManagerDecorator;
	}
	
	
	
	/* ============================= CONTRACT  ============================= */
	
	public static Manager<ContractEntity> createNetworkResilianceContractManager(Peer peer, String who) {
		ManagerAdapter<ContractEntity> adapter = new ManagerAdapter<ContractEntity>(new ContractSyncManagerImpl());
		NetworkContractManagerDecorator networkD = new NetworkContractManagerDecorator (adapter, peer, who);
		ResilienceContractManagerDecorator resiNetworkD = new ResilienceContractManagerDecorator(networkD, peer);
		return resiNetworkD;
	}
	
	
	
	
	
}
