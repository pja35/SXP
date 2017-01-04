package network.impl.messages;

import network.api.annotation.MessageElement;
import network.impl.MessagesImpl;

/**
 * 
 * @author soriano
 *
 */
public class EstablisherMessage extends MessagesImpl 
{
	@MessageElement("receiver")
	private String receiverUid;
	
	@MessageElement("title")
	private String title;
	
	@MessageElement("type")
	private String type = "establisher";
	
	@MessageElement("promI")
	private String promI;
	
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
	 * @param dest
	 */
	public void setReceiver(String source) {
		this.receiverUid = source;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getReceiver() {
		return receiverUid;
	}


	/**
	 * 
	 * @return
	 */
	public String getPromI() {
		return promI;
	}

	/**
	 * 
	 * @param Prom_i(k) (cf sigma protocols in SXP)
	 */
	public void setPromI(String p) {
		this.promI = p;
	}
}
