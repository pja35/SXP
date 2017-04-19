package controller.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkChangeSet;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.UnitOfWork;

import crypt.api.annotation.ParserAction;
import crypt.api.annotation.ParserAnnotation;
import crypt.factories.ParserFactory;
import model.api.Manager;
import model.api.ManagerDecorator;
import model.api.ManagerListener;
import model.entity.Item;
import model.entity.User;

/**
 * 
 * @author Radoua Abderrahim
 *
 */
public class CryptoItemManagerDecorator extends ManagerDecorator<Item>{
	
	private User user;
	
	public CryptoItemManagerDecorator(Manager<Item> em,User user) {
		super(em);
		this.user = user;
	}
	
	@Override
	public boolean persist(Item entity) {
		
		ParserAnnotation parser = ParserFactory.createDefaultParser(entity, user);
		
		entity =(Item) parser.parseAnnotation(ParserAction.SigneAction);
		
		return super.persist(entity);
		
	}

	@Override
	public void findOneById(String id, final ManagerListener<Item> l) {
		
		super.findOneById(id,new ManagerListener<Item>() {
			
			@Override
			public void notify(Collection<Item> results) {
				
				Item item = results.iterator().next();
				
				ParserAnnotation parser = ParserFactory.createDefaultParser(item, user);
				
				item = (Item) parser.parseAnnotation(ParserAction.CheckAction);
				
				ArrayList<Item> rest = new ArrayList<>();
				
				if(item !=null){
					rest.add(item);
				}
				
				l.notify(rest);
			}
		});
	}

	@Override
	public void findAllByAttribute(String attribute, String value, final ManagerListener<Item> l) {
		
		super.findAllByAttribute(attribute, value, new ManagerListener<Item>() {
			
			@Override
			public void notify(Collection<Item> results) {
				
				ArrayList<Item> rest = new ArrayList<>();
				
				for (Iterator iterator = results.iterator(); iterator.hasNext();) {
					
					Item item = (Item) iterator.next();
					
					ParserAnnotation parser = ParserFactory.createDefaultParser(item, user);
					
					item = (Item) parser.parseAnnotation(ParserAction.CheckAction);
					
					if(item != null){
						rest.add(item);
					}
				}
				
				l.notify(rest);
			}
		});
		
	}

	@Override
	public void findOneByAttribute(String attribute, String value, final ManagerListener<Item> l) {
		
		super.findOneByAttribute(attribute, value, new ManagerListener<Item>() {
			
			@Override
			public void notify(Collection<Item> results) {
				
				Item item = results.iterator().next();
				
				ParserAnnotation parser = ParserFactory.createDefaultParser(item, user);
				
				item = (Item) parser.parseAnnotation(ParserAction.CheckAction);
				
				ArrayList<Item> rest = new ArrayList<>();
				
				if(item != null){
					rest.add(item);
				}
				
				l.notify(rest);
			}
		});
	}

	
	@Override
	public boolean end() {
		/*
		final JpaEntityManager jpaEntityManager = (JpaEntityManager) this.getEm().getDelegate();
		final UnitOfWorkChangeSet changeSet = (UnitOfWorkChangeSet) jpaEntityManager.getUnitOfWork().getCurrentChanges();
		UnitOfWorkImpl uow = (UnitOfWorkImpl) this.getEm().unwrap(UnitOfWork.class);
		Collection<Item> collection = this.watchlist();
		
		if(changeSet.hasChanges() && collection.size()==1){
				
				Item item = collection.iterator().next();
				
				ParserAnnotation parser = ParserFactory.createDefaultParser(item, user);
				
				final ObjectChangeSet objectChangeSet = (ObjectChangeSet) changeSet.getObjectChangeSetForClone(item);
				
				if(objectChangeSet.hasChangeFor("title") || objectChangeSet.hasChangeFor("description")){
					item =(Item) parser.parseAnnotation(ParserAction.SigneAction);
					changeSet.mergeObjectChanges(objectChangeSet, changeSet);
				}
		}
		*/
		/*
		final ObjectChangeSet objectChangeSet = changeSet.getObjectChangeSetForClone(bean);
		 
		// Get a list of changed propertys and do something with that.
		final List<String> changedProperties = objectChangeSet.getChanges();
		for(final String property : changedProperties) {
		    System.out.println("Changed property: '" + property);
		}
		 
		// Check if a property called "coolProperty" has changed.
		final ChangeRecord coolPropertyChanges = objectChangeSet.getChangesForAttributeNamed("coolProperty");
		if(coolPropertyChanges != null) {
		    System.out.println("Property 'coolProperty' has changed from '" + coolPropertyChanges.getOldValue() + "' to '" + bean.getCoolProperty() + "'");
		}
		*/
		
		
		
		return super.end();
	}
	
	
}
