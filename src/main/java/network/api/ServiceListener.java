package network.api;

import java.util.EventListener;

/**
 * Interface for services listeners
 * @author Julien Prudhomme
 *
 */
public interface ServiceListener extends EventListener {
	/**
	 * Notify a service listener that messages were received
	 * @param messages
	 */
	public void notify(Messages messages);
}
