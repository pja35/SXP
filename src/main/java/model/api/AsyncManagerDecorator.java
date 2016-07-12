package model.api;

/**
 * Entities manager. Handle local and distant storage, search.
 * @author Julien Prudhomme
 *
 * @param <T>
 */
public abstract class AsyncManagerDecorator<Entity> implements AsyncManager<Entity>{

	private AsyncManager<Entity> em;
	
	public AsyncManagerDecorator(AsyncManager<Entity> em) {
		this.em = em;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void findOneById(String id, AsyncManagerListener<Entity> l) {
		em.findOneById(id, l);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void findAllByAttribute(String attribute, String value, AsyncManagerListener<Entity> l) {
		em.findAllByAttribute(attribute, value, l);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void findOneByAttribute(String attribute, String value, AsyncManagerListener<Entity> l) {
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
