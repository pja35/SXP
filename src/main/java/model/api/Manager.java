package model.api;

/**
 * Asynchronous entity manager. Can handle network things
 * @author Julien Prudhomme
 *
 * @param <Entity>
 */
public interface Manager<Entity> {
	
	/**
	 * Find one entity by its id
	 * @param id
	 * @param l
	 */
	public void findOneById(String id, ManagerListener<Entity> l);
	
	/**
	 * Find all entity that match attribute
	 * @param attribute
	 * @param value
	 * @param l
	 */
	public void findAllByAttribute(String attribute, String value, ManagerListener<Entity> l);
	
	/**
	 * Find one that match attribute
	 * @param attribute
	 * @param value
	 * @param l
	 */
	public void findOneByAttribute(String attribute, String value, ManagerListener<Entity> l);
	
	/**
	 * Persist the entity in the manager
	 * @param entity
	 */
	public void persist(Entity entity);
	
	/**
	 * Begin a transaction
	 */
	public void begin();
	
	/**
	 * End a transaction
	 */
	public void end();
}
