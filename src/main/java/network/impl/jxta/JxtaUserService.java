package network.impl.jxta;

import network.api.SearchListener;
import network.api.UserService;

public class JxtaUserService extends JxtaService implements UserService{
	public static final String NAME = "users";
	public JxtaUserService() {
		this.name = NAME;
	}
	
	@Override
	public void search(String attribute, String value, SearchListener<?> sl) {
		super.search(attribute, value, sl);
	}
	
}
