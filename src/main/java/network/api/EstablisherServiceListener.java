package network.api;

import java.util.EventListener;

public interface EstablisherServiceListener extends EventListener {
	
	/**
	 * Notify when the establisher receives an EstablisherAdvertisement or a Message
	 * @param title : message title
	 * @param data : content
	 * @param senderKey : id for sender
	 */
	public void notify(String title, String data, String senderKey);
}
