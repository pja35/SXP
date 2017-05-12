package controller.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import javax.persistence.Entity;

import org.eclipse.persistence.internal.jpa.metadata.structures.ArrayAccessor;

import controller.Application;
import crypt.api.annotation.ParserAction;
import crypt.api.annotation.ParserAnnotation;
import crypt.factories.ParserFactory;
import model.api.Manager;
import model.api.ManagerDecorator;
import model.api.ManagerListener;
import model.api.UserSyncManager;
import model.entity.User;
import model.factory.ManagerFactory;
import model.syncManager.UserSyncManagerImpl;
import network.api.Peer;
import model.entity.ElGamalSignEntity;
import model.entity.Item;
import model.entity.Message;

/**
 * 
 * @author Radoua Abderrahim
 *
 */
public class CryptoMessageManagerDecorator extends ManagerDecorator<Message>{
	
	private User user;
	private String who;
	
	public CryptoMessageManagerDecorator(Manager<Message> em,String who,User user) {
		super(em);
		this.user = user;
		this.who = who;
	}
	
	@Override
	public boolean persist(Message entity) {
		
		ParserAnnotation parser = ParserFactory.createDefaultParser(entity, user.getKey());
		
		entity = (Message) parser.parseAnnotation(ParserAction.SigneAction,ParserAction.CryptAction);
		
		return super.persist(entity);
	}

	@Override
	public void findAllByAttribute(String attribute, String value, final ManagerListener<Message> l) {
		
		super.findAllByAttribute(attribute, value, new ManagerListener<Message>() {
			@Override
			public void notify(Collection<Message> results) {
				
				ArrayList<Message> res = new ArrayList<>(); 
				
				for (Iterator iterator = results.iterator(); iterator.hasNext();) {
					
					Message message = (Message) iterator.next();
					
					ParserAnnotation<Message> parser;
					
					final ArrayList<User> users = new ArrayList<>(); 
					
					if(message.getReceiverId() == user.getId()){
						
						users.add(user);
						
					}else{
						
						Manager<User> em = ManagerFactory.createNetworkResilianceUserManager(Application.getInstance().getPeer(), who);
						
						Hashtable<String, Object> query = new Hashtable<>(); 
					
						query.put("nick", message.getReceiverName());
						query.put("id", message.getReceiverId());
						
						em.findAllByAttributes(query, new ManagerListener<User>() {
							
							@Override
							public void notify(Collection<User> results) {
								User u = results.iterator().next();
								users.add(u);
							}
						});
						
					}
					
					if(!users.isEmpty()){
						
						parser = ParserFactory.createDefaultParser(message, users.get(0).getKey());
						message = (Message) parser.parseAnnotation(ParserAction.DecryptAction,ParserAction.CheckAction);
						
						if(message != null){
							res.add(message);
						}
					}
					
				}
				
				l.notify(res);
			}
		});
	}
	
	@Override
	public boolean end() {
		/*
		Collection<Message> collection = this.changesInWatchlist();
		
		for (Message message : collection) {
			
				ParserAnnotation parser = ParserFactory.createDefaultParser(message, user.getKey());
				
				message = (Message) parser.parseAnnotation(ParserAction.SigneAction,ParserAction.CryptAction);			
		}
		*/
		return super.end();
	}
}
