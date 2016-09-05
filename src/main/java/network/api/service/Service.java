package network.api.service;

import network.api.Messages;
import network.api.Peer;
import network.api.SearchListener;
import network.api.ServiceListener;
import network.api.advertisement.Advertisement;

/**
 * Service running on a peer. Can handle advertisements.
 * The service should be added on a {@link Peer} via {@link Peer#addService(Service)}
 * The peer will call {@link Service#initAndStart(Peer)}
 * @author Julien Prudhomme
 *
 */
public interface Service {
	
	/**
	 * Service's name. It's used to identify the service and retrieve it via {@link Peer#getService(String)}
	 * @return the service name.
	 */
	public String getName();
	
	/**
	 * Search features on this service. The results will be notified on a {@link SearchListener}
	 * @param sl a search listener
	 */
	public void search(String attribute, String value, SearchListener<?> sl);
	
	/**
	 * Publish an {@link Advertisement} on this service
	 * @param adv
	 */
	public void publishAdvertisement(Advertisement adv);
	
	/**
	 * Init and starts this service for this peer
	 * This method should me called by a {@link Peer} instance
	 * when added via {@link Peer#addService(Service)}
	 * @param peer the peer that serve that service
	 */
	public void initAndStart(Peer peer);
	
	/**
	 * Add a listener to this service
	 * @param l the listener
	 * @param who can be null - only who match will be notified
	 */
	public void addListener(ServiceListener l, String who);
	
	public void removeListener(String who);
	/**
	 * Send messages to several peer (uris)
	 * @param messages
	 * @param uris
	 */
	public void sendMessages(Messages messages, String ...uris);
}
