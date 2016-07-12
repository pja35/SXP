package network.api;

/**
 * Interface for services listeners
 * @author Julien Prudhomme
 *
 */
public interface ServiceListener {
	/**
	 * Notify a service listener that messages were received
	 * @param messages
	 */
	public void notify(Messages messages);
}
