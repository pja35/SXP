package network.api.advertisement;

public interface MessageAdvertisementInterface extends Advertisement {

	public String getSenderId();

	public void setSenderId(String senderId);

	public String getReceiverId();

	public void setReceiverId(String receiverId);

}
