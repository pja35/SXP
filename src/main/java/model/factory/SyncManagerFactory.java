package model.factory;

import model.api.ItemSyncManager;
import model.api.MessageSyncManager;
import model.api.UserSyncManager;
import model.syncManager.UserSyncManagerImpl;
import model.syncManager.ItemSyncManagerImpl;
import model.syncManager.MessageSyncManagerImpl;

public class SyncManagerFactory {
	public static UserSyncManager createUserSyncManager() {
		return new UserSyncManagerImpl();
	}
	public static ItemSyncManager createItemSyncManager() {
		return new ItemSyncManagerImpl();
	}
	public static MessageSyncManager createMessageSyncManager() {
		return new MessageSyncManagerImpl();
	}
}
