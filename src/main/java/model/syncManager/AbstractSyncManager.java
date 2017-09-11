package model.syncManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.sessions.changesets.ObjectChangeSet;

import controller.tools.LoggerUtilities;
import model.validator.EntityValidator;


public abstract class AbstractSyncManager<Entity> implements model.api.SyncManager<Entity>{
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
			LoggerUtilities.logStackTrace(e);
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
			LoggerUtilities.logStackTrace(e);
			return null;
		}

	}

	@Override
	@SuppressWarnings("unchecked")
	public Entity findOneByAttribute(String attribute, String value) {
		Query q = em.createQuery("select t from " + theClass.getSimpleName() + " t where t."+ attribute + "=:value");
		q.setParameter("value", value);
		try {
			return (Entity) q.getSingleResult();
		} catch(Exception e) {
			LoggerUtilities.logStackTrace(e);
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
			LoggerUtilities.logStackTrace(e);
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
			LoggerUtilities.logStackTrace(e);
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
			em.clear(); // Should it be done here or before the close method?
			return true;
		}catch(Exception e){
			LoggerUtilities.logStackTrace(e);
			return false;
		}
	}

	@Override
	public boolean persist(Entity entity) {
		try{
			em.persist(entity);
			return true;
		}catch(Exception e){
			System.out.println(e);
			LoggerUtilities.logStackTrace(e);
			return false;
		}
	}

	@Override
	public boolean remove(Entity entity){
		try{
			em.remove(entity);
			return true;
		}catch(Exception e){
			LoggerUtilities.logStackTrace(e);
			return false;
		}
	}

	@Override
	public boolean contains(Entity entity){
		try{
			return em.contains(entity);
		}catch(Exception e){
			LoggerUtilities.logStackTrace(e);
			return false;
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<Entity> watchlist() {
		UnitOfWorkImpl uow = (UnitOfWorkImpl) em.unwrap(UnitOfWork.class);
		return uow.getCloneMapping().keySet();
	}

	@Override
	public boolean close() {
		try{
			em.close();
			em = null;
			return true;
		}catch(Exception e){
			LoggerUtilities.logStackTrace(e);
			return false;
		}
	}

	@Override
	public Collection<Entity> changesInWatchlist() {
		
		ArrayList<Entity> res = new ArrayList<>();
		
		UnitOfWorkImpl uow = (UnitOfWorkImpl) em.unwrap(UnitOfWork.class);
		
		final UnitOfWorkChangeSet changeSet = (UnitOfWorkChangeSet) uow.getCurrentChanges();
		
		Collection<Entity> collection = uow.getCloneMapping().keySet(); 
		
		for (Iterator<Entity> iterator = collection.iterator(); iterator.hasNext();) {
			
			Entity entity = iterator.next();
			
		    ObjectChangeSet objectChangeSet = changeSet.getObjectChangeSetForClone(entity);
			
			if(objectChangeSet.hasChanges()){
				res.add(entity);
			}
			
		}
		
		return res;
	}
	
	
}
