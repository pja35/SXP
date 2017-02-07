package model.syncManager;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.RollbackException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;

import controller.tools.LoggerUtilities;
import model.entity.Item;
import model.factory.ValidatorFactory;
import model.validator.EntityValidator;
import model.validator.ItemValidator;


public abstract class AbstractSyncManager<Entity> implements model.api.SyncManager<Entity>{
	private final static Logger log = LogManager.getLogger(AbstractSyncManager.class);
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
		try
		{
			return (Entity) em.find(theClass, id);

		}
		catch(Exception e)
		{
			log.info(LoggerUtilities.getStackTrace(e));
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Entity> findAll() {
		try
		{
			Query q = em.createQuery("select t from " + theClass.getSimpleName() + " t");
			return q.getResultList();
		}
		catch(Exception e)
		{
			log.info(LoggerUtilities.getStackTrace(e));
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public Entity findOneByAttribute(String attribute, String value) {
		Query q = em.createQuery("select t from " + theClass.getSimpleName() + " t where t."+ attribute + "=:value");
		q.setParameter("value", value);
		try {
			return (Entity) q.getSingleResult();
		} catch(Exception e) {
			log.info(LoggerUtilities.getStackTrace(e));
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
			log.info(LoggerUtilities.getStackTrace(e));
			return null;
		}
	}

	@Override
	public boolean begin() {
		try
		{
			em.getTransaction().begin();
			return true;
		}
		catch(Exception e)
		{
			log.info(LoggerUtilities.getStackTrace(e));
			return false;
		}
	}

	protected abstract EntityValidator<?> getAdaptedValidator();

	@Override
	public boolean check(){
		@SuppressWarnings("unchecked")
		EntityValidator<Entity> ev = (EntityValidator<Entity>) this.getAdaptedValidator();
		boolean ret = true;
		for (Entity ent : this.watchlist()) {
			ev.setEntity(ent);
			ret = ret && ev.validate();
		}
		return ret;
	}
	
	@Override
	public boolean end() {
		//Validate all the entities in the Watchlist
		if(! this.check()){return false;}
		try{
			EntityTransaction emtr = em.getTransaction();
			if (!emtr.getRollbackOnly())
				em.getTransaction().commit();
			em.clear(); //Ici on détache toutes les entitées. Devrait-on plutot fermer (em.close()) ?
			return true;
		}catch(Exception e){
			log.info(LoggerUtilities.getStackTrace(e));
			return false;
		}
	}

	@Override
	public boolean persist(Entity entity) {
		try{
			em.persist(entity);
			return true;
		}catch(Exception e){
			log.info(LoggerUtilities.getStackTrace(e));
			return false;
		}
	}

	@Override
	public boolean remove(Entity entity){
		try{
			em.remove(entity);
			return true;
		}catch(Exception e){
			log.info(LoggerUtilities.getStackTrace(e));
			return false;
		}
	}

	@Override
	public boolean contains(Entity entity){
		try{
			return em.contains(entity);
		}catch(Exception e){
			log.info(LoggerUtilities.getStackTrace(e));
			return false;
		}
	}

	/**
	 * Ici on fait des choses pas très propres !
	 * Des casts pas trop vérifiés, il faut croiser les doigts
	 * Des accès à des méthodes bien cachées qu'on ne comprend pas tout à fait,...
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Entity> watchlist() {
		//EntityManagerImpl emi = (EntityManagerImpl)em;
		EntityManagerImpl emi = em.unwrap(EntityManagerImpl.class);
		Map<Object,Object> wlMap = emi.getActivePersistenceContext(null).getCloneMapping();
		return (Collection<Entity>) wlMap.keySet();
	}
}
