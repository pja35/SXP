package network.api.advertisement;


import org.jdom2.Document;

import crypt.api.signatures.Signable;
import network.api.Peer;
import network.api.service.Service;

/**
 * Advertise a feature on a {@link Service}
 * @param <Sign> signature's type
 * @see Signable
 * @author Julien Prudhomme
 *
 */
public interface Advertisement {
	/**
	 * Get the advertisement name
	 * @return the advertisement's name
	 */
	public String getName();
	
	/**
	 * Get the advertisement type
	 * @return
	 */
	public String getAdvertisementType();
	
	/**
	 * Publish this advertisement on the network
	 * @see Peer
	 * @param peer
	 */
	public void publish(Peer peer);
	
	/**

	 * Initialize this advertisement with an xml document
	 * @param doc
	 */
	public void initialize(Document doc);
	
	/**
	 * Generate an xml document describing this advertisement
	 * @return
	 */
	public Document getDocument();
	
	/**
	 * Get a string array of field that are used for indexing this advertisement
	 * @return
	 */
	public String[] getIndexFields();
	
	/**
	 * Get the peer id (URI) source of the advertisement
	 * @return
	 */
	public String getSourceURI();
	
	public void setSourceURI(String uri);
	
}
