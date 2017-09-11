package model.api;

import java.util.Collection;
import java.util.Hashtable;

import javax.persistence.EntityManager;

/**
 * General interface for entity managers
 * @author Julien Prudhomme
 *
 * @param <Entity> class' entity
 */
public interface SyncManager<Entity> {
	/**
	 * Initialise the entity manager with the unit name
	 * @param unitName unit (entity) name for persistance. See persistance.xml in META-INF
	 */
	public void initialisation(String unitName, Class<?> c);

	/**
	 * Find one entity with its Id
	 * @param id entity id
	 * @return An instance of the entity or null.
	 */
	public Entity findOneById(String id);

	/**
	 * Return the whole collection of stored entities
	 * @return A collection of entities
	 */
	public Collection<Entity> findAll();

	/**
	 * Find all entry with corresponding att/value
	 * @param attribute
	 * @param value
	 * @return
	 */
	public Collection<Entity> findAllByAttribute(String attribute, String value);
	
	/**
	 * Return an object corresponding to the attribute/value
	 * @param attribute
	 * @param value
	 * @return
	 */
	public Entity findOneByAttribute(String attribute, String value);

	/**
	 * Persist(insert) this instance to the database
	 * @param entity
	 * @return true if done
	 */
	public boolean persist(Entity entity);
	
	/**
	 * Begin the transaction
	 * @return true if done
	 */
	public boolean begin();

	/**
	 * end (commit) the transaction
	 * @return true if done
	 */
	public boolean end();

	/**
	 * Checks if all the managed entities (i.e. the "watchlist") are valid entities.
	 * If the validation at persist() call is activated (default) this will always return true.
	 * Go to bin/META-INF/persistence.xml to change validation mode
	 * @return true if all the managed entities are valid, false otherwise
	 */
	public boolean check();

	/**
	 * Checks if a given entity is in 'managed' state for this manager.
	 * @param entity
	 * @return true if entity is managed, false otherwise
	 */
	public boolean contains(Entity entity);

	/**
	 * Gives a list of the entities currently in 'managed' state.
	 */
	public Collection<Entity> watchlist();

	/**
	 * Returns a list of changed entities that currently in "managed" state.
	 */
	public Collection<Entity> changesInWatchlist();
	
	/**
	 * Remove an entity from the DB
	 * @param an entity
	 * @return True if the entity has been removed, false otherwise
	 */
	public boolean remove(Entity entity);
	
	/**
	 * Close the entity manager
	 * @return false if the operation fails (exceptions are logged)
	 */
	public boolean close();
	
}
