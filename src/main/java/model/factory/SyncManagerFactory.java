package model.factory;

import model.api.ContractSyncManager;
import model.api.ItemSyncManager;
import model.api.MessageSyncManager;
import model.api.UserSyncManager;
import model.syncManager.ContractSyncManagerImpl;
import model.syncManager.ItemSyncManagerImpl;
import model.syncManager.MessageSyncManagerImpl;
import model.syncManager.UserSyncManagerImpl;

public class SyncManagerFactory {
	public static UserSyncManager createUserSyncManager() {
		return new UserSyncManagerImpl();
	}
	public static ItemSyncManager createItemSyncManager() {
		return new ItemSyncManagerImpl();
	}
	public static ContractSyncManager createContractSyncManager() {
		return new ContractSyncManagerImpl();
	}
	public static MessageSyncManager createMessageSyncManager() {
		return new MessageSyncManagerImpl();
	}
}
