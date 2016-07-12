package model.persistance;

import model.entity.Item;

public class ItemManager extends AbstractEntityManager<Item> {
	
	public ItemManager() {
		super();
		this.initialisation("persistence", Item.class);
	}
}
