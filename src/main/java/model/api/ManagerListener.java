package model.api;

import java.util.Collection;

/**
 * AsyncManager listener
 * @author Julien Prudhomme
 *
 * @param <Entity>
 */
public interface ManagerListener<Entity> {
	/**
	 * method called when one or more results are available.
	 * @param results
	 */
	public void notify(Collection<Entity> results);
}
