package model.api;

import java.util.Collection;

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
	 */
	public void persist(Entity entity);
	
	/**
	 * Begin the transaction
	 */
	public void begin();
	
	/**
	 * end (commit) the transaction
	 */
	public void end();
}
