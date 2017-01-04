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
	 * 		Sender Id
	 * @param whoUid
	 * 		Sender location
	 * @param promI
	 * 		Prom_i(k) (cf SXP website) 
	 * @param uris
	 * 		Receiver location
	 * @return
	 * 		contrat envoyé
	 */
	public EstablisherMessage sendPromI(String title, String whoId, String whoUid, String promI, String ...uris);
	
	
	public static final String NAME = "establisher";

}
