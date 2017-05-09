package network.impl.jxta;

import network.api.UserService;

public class JxtaMessageService extends JxtaService implements UserService{
	public static final String NAME = "messages";
	public JxtaMessageService() {
		this.name = NAME;
	}
}
