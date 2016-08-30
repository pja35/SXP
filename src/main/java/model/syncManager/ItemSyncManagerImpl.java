package model.syncManager;

import model.api.ItemSyncManager;
import model.entity.Item;

public class ItemSyncManagerImpl extends AbstractSyncManager<Item> implements ItemSyncManager {
	
	public ItemSyncManagerImpl() {
		super();
		this.initialisation("persistence", Item.class);
	}
}
