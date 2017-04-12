package crypt.utils;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import crypt.api.annotation.ParserAction;
import crypt.api.annotation.ParserAnnotation;
import crypt.api.hashs.Hasher;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.factories.HasherFactory;
import crypt.factories.ParserFactory;
import model.api.UserSyncManager;
import model.entity.Item;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;

public class CryptoParserTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testParserSigne() {
		System.out.println("Test begin !");
		
		String password = "123456";
		
		User user = new User();
		user.setNick("radoua");
		Hasher hasher = HasherFactory.createDefaultHasher();
		user.setSalt(HasherFactory.generateSalt());
		hasher.setSalt(user.getSalt());
		user.setPasswordHash(hasher.getHash(password.getBytes()));
		user.setCreatedAt(new Date());
		user.setKey(ElGamalAsymKeyFactory.create(false));
		user.setId("IdRadouaUser");
		
		Item item  = new Item();
		item.setCreatedAt(new Date());
		item.setUsername(user.getNick());
		item.setPbkey(user.getKey().getPublicKey());
		item.setUserid(user.getId());
		
		item.setTitle("title");
		item.setDescription("description");
		
		ParserAnnotation parser = ParserFactory.createDefaultParser(item, user);
		
		item = (Item) parser.parseAnnotation(ParserAction.SigneAction);
		
		if(item.getSignature() != null){
			System.out.println("l'objet est signé :");
		}
		
		assertTrue("objet signé",item.getSignature()!=null);
		
		//modifier l'objet
		item.setTitle("titre différent");
		item.setDescription("autre chose ...");
		
		ParserAnnotation parser2 = ParserFactory.createDefaultParser(item, user);
		
		item = (Item) parser2.parseAnnotation(ParserAction.CheckAction);
		
		if(item != null){
			System.out.println("la signature est bien verifier");
		}else{
			System.out.println("l'objet est different");
		}
		
		assertFalse("objet different",item!=null);
	}
	
	

}
