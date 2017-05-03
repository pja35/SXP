package network.impl.jxta;

import network.api.UserService;

public class JxtaUserService extends JxtaService implements UserService{
	public static final String NAME = "users";
	public JxtaUserService() {
		this.name = NAME;
	}
}
