package network.impl.jxta;

import network.api.ItemService;
import network.api.SearchListener;

public class JxtaItemService extends JxtaService implements ItemService{
	public static final String NAME = "items";
	public JxtaItemService() {
		this.name = NAME;
	}
	@Override
	public void search(String attribute, String value, SearchListener<?> sl) {
		System.out.println("P2P search for items : {"+attribute+":"+value+"}");
		super.search(attribute, value, sl);
	}
	
}
