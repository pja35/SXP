package network.impl.messages;

import network.api.annotation.MessageElement;
import network.impl.MessagesImpl;

/**
 * 
 * @author Nathanaël EON
 *
 */
public class EstablisherMessage extends MessagesImpl 
{
	@MessageElement("title")
	private String title;

	@MessageElement("source")
	private String sourceUri;
	
	@MessageElement("type")
	private String type = "establisher";
	
	@MessageElement("contract")
	private String contract;
	
	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * 
	 * @param source
	 */
	public void setSource(String source) {
		this.sourceUri = source;
	}
	/**
	 * 
	 * @return
	 */
	public String getSource() {
		return sourceUri;
	}

	/**
	 * 
	 * @return
	 */
	public String getContract() {
		return contract;
	}
	/**
	 * 
	 * @param Message content
	 */
	public void setContract(String c) {
		this.contract = c;
	}
}
