package network.impl.messages;

import network.api.annotation.MessageElement;
import network.impl.MessagesImpl;

public class RequestMessageUserMessage extends MessagesImpl{
	
	@MessageElement("source")
	private String sourceUri;
	
	@MessageElement("senderId")
	private String senderId;
	
	@MessageElement("receiverId")
	private String receiverId;
	
	@MessageElement("type")
	private String type = "request";
	
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

	public void setSource(String source) {
		this.sourceUri = source;
	}
	
	public String getSource() {
		return sourceUri;
	}

	public String getType() {
		return type;
	}

}
