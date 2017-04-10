package controller.managers;

import javax.persistence.Entity;

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
		
		entity = (Item) parser.parseAnnotation(ParserAction.SigneAction);
		
		return super.persist(entity);
		
	}


	@Override
	public void findOneById(String id, ManagerListener<Item> l) {
		super.findOneById(id,l);
		
	}


	
	
	
	
	
}
