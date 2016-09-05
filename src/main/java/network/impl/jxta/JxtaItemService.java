package network.impl.jxta;

import network.api.ItemService;

public class JxtaItemService extends JxtaService implements ItemService{
	public static final String NAME = "items";
	public JxtaItemService() {
		this.name = NAME;
	}
}
