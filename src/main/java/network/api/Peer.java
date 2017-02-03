package network.api;

import java.io.IOException;
import java.util.Collection;

import net.jxta.exception.PeerGroupException;
import network.api.service.InvalidServiceException;
import network.api.service.Service;

/**
 * Interface for the whole peer
 * @author Julien Prudhomme
 *
 */
public interface Peer {
	/**
	 * Starts the peer
	 * @param cache cache folder
	 * @param port listening port
	 * @throws IOException
	 * @throws RuntimeException 
	 * @throws PeerGroupException 
	 */
	public void start(String cache, int port, String ...ips) throws IOException, PeerGroupException, RuntimeException;
	
	/**
	 * Stop the server
	 */
	public void stop();
	
	/**
	 * Should return this Peer public IP address
	 * @return a String that represent an IP address
	 */
	public String getIp();
	
	/**
	 * Get a collection of all services supported by this peer
	 * @return
	 */
	public Collection<Service> getServices();
	
	/**
	 * Get a service by its name
	 * @param name The service's name
	 * @return The Service
	 */
	public Service getService(String name);
	
	/**
	 * Add a service to this Peer
	 * @param service
	 * @throws InvalidServiceException : the service is not a valid service
	 */
	public void addService(Service service) throws InvalidServiceException;
	
	/**
	 * Return a string representation of the peer id (uri)
	 * @return
	 */
	public String getUri();
	
	public void bootstrap(String ...ips);
}
