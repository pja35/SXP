package network.impl;

import network.api.Messages;
import network.api.ServiceListener;

public class EstablisherListener implements ServiceListener {

	@Override
	public void notify(Messages messages) {
		if(messages.getMessage("type").equals("establisher"))
		{
				System.out.println("Receiver :" + messages.getWho() + "\nTitre :" + messages.getMessage("title") + "\nContenu : " + messages.getMessage("promI") + "\n");
		}
	}

}
