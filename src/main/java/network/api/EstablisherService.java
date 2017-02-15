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
	 * 		titre du message
	 * @param who
	 * 		An id (the name we will put a listener on)
	 * @param promI
	 * 		Prom_i(k) (cf SXP website) 
	 * @param uris
	 * 		Receiver location
	 * @return
	 * 		sent contract
	 */
	public EstablisherMessage sendPromI(String title, String who, String promI, String ...uris);
	
	
	public static final String NAME = "establisher";

}
