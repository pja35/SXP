package network.impl.advertisement;

import network.api.advertisement.MessageAdvertisementInterface;
import network.api.annotation.AdvertisementAttribute;
import network.api.annotation.MessageElement;
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
	private String senderId;
	
	@AdvertisementAttribute(indexed = true)
	private String receiverId;

	
	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	@Override
	public String getName() {
		return "message";
	}

	@Override
	public String getAdvertisementType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
}
