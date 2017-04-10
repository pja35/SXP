package controller.managers;

import javax.persistence.Entity;

import crypt.api.annotation.ParserAction;
import crypt.api.annotation.ParserAnnotation;
import crypt.factories.ParserFactory;
import model.api.Manager;
import model.api.ManagerDecorator;
import model.api.ManagerListener;
import model.entity.User;

/**
 * 
 * @author Radoua Abderrahim
 *
 */
public class CryptoUserManagerDecorator extends ManagerDecorator<User>{
	
	private User user;
	
	public CryptoUserManagerDecorator(Manager<User> em,User user) {
		super(em);
		this.user = user;
	}
	
	
	@Override
	public boolean persist(User entity) {
		
		ParserAnnotation parser = ParserFactory.createDefaultParser(entity, user);
		
		entity = (User) parser.parseAnnotation(ParserAction.HasherAction);
		
		return super.persist(entity);
		
	}


	
	
	
	
	
}
