/**
 * 
 */
package network.api;

import java.util.HashMap;

import crypt.api.key.AsymKey;
import network.api.service.Service;
import network.impl.messages.EstablisherMessage;

/**
 * @author soriano
 *
 */
public interface EstablisherService extends Service 
{
	
	public static final String NAME = "establisher";
	
	
	/**
	 * Encapsulate contract sending (using either Message or Advertisement)
	 * @param title : event title
	 * @param data : data sent
	 * @param senderId : sender id 
	 * @param uris : receiving peers uris, if null : uses Advertisement, otherwise uses Messages
	 * @param peer : the sending peer
	 */
	public <Key extends AsymKey<?>> void sendContract(String title, String data, String senderId, Peer peer, HashMap<Key,String> uris);
	
	/**
	 * Send a "Message" (synchrone)
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
	
	/**
	 * Send an EstablisherAdvertisement (asynchrone)
	 * @param title : advertisement title
	 * @param data : data to be sent
	 * @param sourceKey : identifier of the sender
	 * @param peer : peer from which it is sent
	 */
	public void sendContract(String title, String data, String sourceKey, Peer peer);

	
	
	/**
	 * Encapsulate listening system
	 * @param field : field to be matched with @param value
	 * @param listenerId : an id to listen on and to be able to remove the listener
	 * @param l : listener notified upon receiving data
	 * @param useMessage : if true -> use Messages, else use Advertisement
	 */
	public void setListener(final String field, final String value, String listenerId, final EstablisherServiceListener l, boolean useMessage);
	/**
	 * Add an advertisement listener and search in already sent adverts
	 * @param field : field to be matched with @param value
	 * @param listenerId : Id to be able to remove the listener
	 * @param l : listener notified when advert received 
	 */
	public void listens(String field, String value, String listenerId, EstablisherServiceListener l);
	
	/*
	 * Remove the previously set listener
	 */
	public void removeListener(String listenerId);
	
}
