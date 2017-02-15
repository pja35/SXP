package network.impl;

import network.api.Messages;
import network.api.ServiceListener;

public class EstablisherListener implements ServiceListener {

	@Override
	public void notify(Messages messages) {
		if(messages.getMessage("type").equals("establisher"))
		{
				System.out.println("\n----ESTABLISHER MESSAGE RECEIVER----\nReceiver :" + messages.getWho() + "\nTitle : " +
								messages.getMessage("title") + "\nMessage : " + messages.getMessage("promI") + "\n----END----\n");
		}
	}

}
