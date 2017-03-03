package network.impl.advertisement;

import network.api.advertisement.MessageAdvertisementInterface;
import network.api.annotation.AdvertisementAttribute;
import network.api.annotation.ServiceName;
import network.impl.AbstractAdvertisement;

/**
 * Advertisement for a peer that host an message
 * @author Julien Prudhomme
 *
 * @param <Sign>
 */
@ServiceName(name = "messages")
public class MessageAdvertisement extends AbstractAdvertisement implements MessageAdvertisementInterface{

	@AdvertisementAttribute(indexed = true)
	private String title;
	
	@Override
	public String getName() {
		return "message";
	}

	@Override
	public String getAdvertisementType() {
		return null;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
}
