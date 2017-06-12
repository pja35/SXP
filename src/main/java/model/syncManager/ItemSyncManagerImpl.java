package model.syncManager;

import model.api.ItemSyncManager;
import model.entity.Item;
import model.factory.ValidatorFactory;
import model.validator.EntityValidator;

public class ItemSyncManagerImpl extends AbstractSyncManager<Item> implements ItemSyncManager {

	public ItemSyncManagerImpl() {
		super();
		this.initialisation("persistence", Item.class);
	}
	@Override
	protected EntityValidator<?> getAdaptedValidator() {
		return ValidatorFactory.createItemValidator();
	}
}
