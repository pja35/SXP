package model.syncManager;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;


public class AbstractSyncManager<Entity> implements model.api.SyncManager<Entity>{
	private EntityManagerFactory factory;
	private EntityManager em;
	private Class<?> theClass;
	@Override
	public void initialisation(String unitName, Class<?> c) {
		factory = Persistence.createEntityManagerFactory(unitName);
		this.theClass = c;
		em = factory.createEntityManager();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Entity findOneById(String id) {
		return (Entity) em.find(theClass, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Entity> findAll() {
		Query q = em.createQuery("select t from " + theClass.getSimpleName() + " t");
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public Entity findOneByAttribute(String attribute, String value) {
		Query q = em.createQuery("select t from " + theClass.getSimpleName() + " t where t."+ attribute + "=:value");
		q.setParameter("value", value);
		try {
			return (Entity) q.getSingleResult();
		} catch(Exception e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Entity> findAllByAttribute(String attribute, String value) {
		Query q = em.createQuery("select t from " + theClass.getSimpleName() + " t where t."+ attribute + "=:value");
		q.setParameter("value", value);
		try {
			return q.getResultList();
		} catch(Exception e) {
			return null;
		}
	}

	@Override
	public void begin() {
		em.getTransaction().begin();
	}

	@Override
	public void end() {
		em.getTransaction().commit();
	}

	@Override
	public void persist(Entity entity) {
		em.persist(entity);
	}
	
}
