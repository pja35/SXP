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
