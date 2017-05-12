/**
 * 
 */
package network.api;

import network.api.service.Service;
import network.impl.messages.EstablisherMessage;

/**
 * @author soriano
 *
 */
public interface EstablisherService extends Service 
{
	/**
	 * 
	 * @param title
	 * 		Message title
	 * @param who
	 * 		An id (the name the receiver will put a listener on)
	 * @param sourceId
	 * 		The user id of the sender
	 * @param contract
	 * 		Content of the message
	 * @param uris
	 * 		Receivers location
	 * @return
	 * 		sent contract
	 */
	public EstablisherMessage sendContract(String title, String who, String sourceId, String contract, String ...uris);
	
	
	public static final String NAME = "establisher";

	public void listens(String field, String value, EstablisherServiceListener l);
}
