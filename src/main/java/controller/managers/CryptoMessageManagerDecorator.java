package controller.managers;

import java.util.Collection;
import java.util.Iterator;

import javax.persistence.Entity;

import crypt.api.annotation.ParserAction;
import crypt.api.annotation.ParserAnnotation;
import crypt.factories.ParserFactory;
import model.api.Manager;
import model.api.ManagerDecorator;
import model.api.ManagerListener;
import model.api.UserSyncManager;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import model.entity.Message;

/**
 * 
 * @author Radoua Abderrahim
 *
 */
public class CryptoMessageManagerDecorator extends ManagerDecorator<Message>{
	
	private User user;
	
	public CryptoMessageManagerDecorator(Manager<Message> em,User user) {
		super(em);
		this.user = user;
	}
	
	@Override
	public boolean persist(Message entity) {
		
		ParserAnnotation parser = ParserFactory.createDefaultParser(entity, user);
		
		entity = (Message) parser.parseAnnotation(ParserAction.CryptAction);
		
		return super.persist(entity);
	}

	@Override
	public void findAllByAttribute(String attribute, String value, final ManagerListener<Message> l) {
		
		super.findAllByAttribute(attribute, value, new ManagerListener<Message>() {
			@Override
			public void notify(Collection<Message> results) {
				
				for (Iterator iterator = results.iterator(); iterator.hasNext();) {
					
					Message message = (Message) iterator.next();
					
					ParserAnnotation parser;
					
					User receiver = null;
					
					if(message.getReceiverId() == user.getId()){
						
						receiver = user;	
						
					}else{
						
						UserSyncManager em = new UserSyncManagerImpl();
					    receiver = em.findOneById(message.getReceiverId());
					    em.close();
					    
					}
					
					parser = ParserFactory.createDefaultParser(message, receiver);
					
					message = (Message) parser.parseAnnotation(ParserAction.DecryptAction);
				}
				
				l.notify(results);
			}
		});
	}
	
	
}
