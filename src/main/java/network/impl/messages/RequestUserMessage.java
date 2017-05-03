package network.impl.messages;

import network.api.annotation.MessageElement;
import network.impl.MessagesImpl;

public class RequestUserMessage extends MessagesImpl{
	
	@MessageElement("source")
	private String sourceUri;
	
	@MessageElement("nick")
	private String nick;
	
	@MessageElement("pbkey")
	private String pbkey;
	
	@MessageElement("type")
	private String type = "request";
	
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getPbkey() {
		return pbkey;
	}

	public void setPbkey(String pbkey) {
		this.pbkey = pbkey;
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
