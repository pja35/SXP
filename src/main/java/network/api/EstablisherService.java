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
	 * @param whoId
	 * 		receiver Id
	 * @param promI
	 * 		Prom_i(k) (cf SXP website) 
	 * @param uris
	 * 		Receiver location
	 * @return
	 * 		contrat envoyé
	 */
	public EstablisherMessage sendPromI(String title, String whoId, String promI, String ...uris);
	
	
	public static final String NAME = "establisher";

}
