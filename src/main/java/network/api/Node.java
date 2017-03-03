package network.api;

import java.io.IOException;

import net.jxta.exception.PeerGroupException;

/**
 * Network Node interface. Handle P2P server.
 * @author Julien Prudhomme
 *
 */
public interface Node {
	/**
	 * Initialize peer to peer node.
	 * @param cacheFolder relative path to the cache folder. The folder will be created if it doesn't exist.
	 * @param name peer name.
	 * @param persistant save configuration on drive if true
	 * @throws IOException cacheFolder read/write error. 
	 */
	public void initialize(String cacheFolder, String name, boolean persistant) throws IOException;
	
	/**
	 * Return if the Node instance is correctly initalized.
	 * @return true if initialized, otherwise false
	 */
	public boolean isInitialized();
	
	/**
	 * Start the P2P server.
	 * @param port the listening port.
	 * @throws RuntimeException the init method was not called before starting server
	 * @throws PeerGroupException : Peer group failed to initialized
	 * @throws IOException : cacheFolder read/write error.
	 */
	public void start(int port) throws RuntimeException, IOException, PeerGroupException;
	
	/**
	 * Return true if the server is succesfully started
	 * @return true if server started
	 */
	public boolean isStarted();
	
	/**
	 * Stop the P2P network
	 */
	public void stop();
}
