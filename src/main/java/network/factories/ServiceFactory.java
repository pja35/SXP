package network.factories;

import network.api.ItemRequestService;
import network.impl.jxta.JxtaItemsSenderService;

public class ServiceFactory {
	public ItemRequestService createItemRequestService() {
		return new JxtaItemsSenderService();
	}
}
