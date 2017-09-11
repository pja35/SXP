package network.impl.jxta;

import network.api.SearchListener;
import network.api.UserService;

public class JxtaMessageService extends JxtaService implements UserService{
	
	public static final String NAME = "messages";
	
	public JxtaMessageService() {
		this.name = NAME;
	}
	
	@Override
	public void search(String attribute, String value, SearchListener<?> sl) {
		super.search(attribute, value, sl);
	}
	
	
}
