package network.api;

import java.util.EventListener;

public interface EstablisherServiceListener extends EventListener {
	
	/*
	 * Notify when the establisher receives an EstablisherAdvertisement or a Message
	 */
	public void notify(String title, String data, String senderKey);
}
