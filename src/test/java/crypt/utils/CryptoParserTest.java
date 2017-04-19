package crypt.utils;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import crypt.api.annotation.ParserAction;
import crypt.api.annotation.ParserAnnotation;
import crypt.api.hashs.Hasher;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.factories.EncrypterFactory;
import crypt.factories.HasherFactory;
import crypt.factories.ParserFactory;
import crypt.impl.encryption.ElGamalEncrypter;
import model.api.UserSyncManager;
import model.entity.ElGamalKey;
import model.entity.Item;
import model.entity.Message;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import util.TestInputGenerator;
//import org.apache.commons.lang.SerializationUtils;


public class CryptoParserTest {
	
	User user;
	
	private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	@Before
	public void setUp() throws Exception {
		String password = "123456";
		user = new User();
		user.setNick("radoua");
		Hasher hasher = HasherFactory.createDefaultHasher();
		user.setSalt(HasherFactory.generateSalt());
		hasher.setSalt(user.getSalt());
		user.setPasswordHash(hasher.getHash(password.getBytes()));
		user.setCreatedAt(new Date());
		user.setKey(ElGamalAsymKeyFactory.create(false));
		user.setId("IdRadouaUser");
	}
	/*
	@Test
	public void parserCryptoActionTest(){
		
	    String text = "un message avec quelque caractere : { _ 0 1 2 3 4 5 6 7 8 9 - + * & é \" [] \\ / àç!è§ }";

		Message message  = new Message();
		message.setSendingDate(new Date());
		message.setSender(user.getId(), user.getNick());
		message.setReceiver(user.getId(), user.getNick());
		message.setMessageContent(text);
		
		ElGamalEncrypter encrypter = EncrypterFactory.createElGamalEncrypter();
		
		encrypter.setKey(user.getKey());
		
		ParserAnnotation parser = ParserFactory.createDefaultParser(message, user);
		
		message = (Message) parser.parseAnnotation(ParserAction.CryptAction);
		
		assertNotEquals(text, message.getMessageContent()); // test if string(text).NotEqual(message.content)  (!=)
		
		message = (Message) parser.parseAnnotation(ParserAction.DecryptAction);
		
		assertEquals(text, message.getMessageContent()); // test if string(text).equal(message.content)   (==)
	}
	*/
	
	@Test
	public void itemJsonTest(){
		
		Item item = new Item();
		
		String title = TestInputGenerator.getRandomAlphaWord();
		String description = TestInputGenerator.getRandomAlphaWord();
		String userid = TestInputGenerator.getRandomAlphaWord();
		String username=TestInputGenerator.getRandomAlphaWord();
		Date date = TestInputGenerator.getTodayDate();
		
		item.setTitle(title);
		item.setDescription(description);
		item.setUserid(userid);
		item.setUsername(username);
		item.setCreatedAt(date);
		item.setPbkey(TestInputGenerator.getRandomBigInteger(16));
		
		ParserAnnotation<Item> parser = ParserFactory.createDefaultParser(item, user);
	    item = parser.parseAnnotation(ParserAction.SigneAction);
	    
	    JsonTools<Item> json = new JsonTools<>(new TypeReference<Item>(){});
		
	    String itemFormJson = json.toJson(item);
	    
	    Item it = json.toEntity(itemFormJson);
		String createdDate = dateFormat.format(it.getCreatedAt());
		assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
		assertTrue(it.getDescription().equals(description));
		assertTrue(it.getId()==null);
		assertTrue(it.getPbkey() != BigInteger.ZERO);
		assertTrue(it.getTitle().equals(title));
		assertTrue(it.getUserid().equals(userid));
		assertTrue(it.getUsername().equals(username));
	    
	    
	}
	
}
