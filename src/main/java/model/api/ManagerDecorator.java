package model.api;

/**
 * Entities manager. Handle local and distant storage, search.
 * @author Julien Prudhomme
 *
 * @param <T>
 */
public abstract class ManagerDecorator<Entity> implements Manager<Entity>{

	private Manager<Entity> em;
	
	public ManagerDecorator(Manager<Entity> em) {
		this.em = em;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void findOneById(String id, ManagerListener<Entity> l) {
		em.findOneById(id, l);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void findAllByAttribute(String attribute, String value, ManagerListener<Entity> l) {
		em.findAllByAttribute(attribute, value, l);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void findOneByAttribute(String attribute, String value, ManagerListener<Entity> l) {
		em.findOneByAttribute(attribute, value, l);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void persist(Entity entity) {
		em.persist(entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void begin() {
		em.begin();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void end() {
		em.end();
	}

}
