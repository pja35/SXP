package network.impl.messages;

import network.api.annotation.MessageElement;
import network.impl.MessagesImpl;

public class RequestUserMessage extends MessagesImpl{
	
	@MessageElement("source")
	private String sourceUri;
	
	@MessageElement("nick")
	private String nick;
	
	@MessageElement("attribute")
	private String attribute;
	
	@MessageElement("type")
	private String type = "request";
	
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
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

	public void setType(String type) {
		this.type = type;
	}
}
