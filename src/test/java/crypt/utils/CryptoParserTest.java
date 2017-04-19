package crypt.utils;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Base64;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;


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
//import org.apache.commons.lang.SerializationUtils;


public class CryptoParserTest {

	@Before
	public void setUp() throws Exception {
	}
	
	
/*
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
*/
	@Test
	public void parserCryptoActionTest(){
		
		String password = "123456";
		String password2 = "abcdef";
	    //String text = "Ce message est crypté de radoua.abderrahim@gmail.com : { _ 0 1 2 3 4 5 6 7 8 9 - + * & é \" [] \\ / àç!è§ }";
	    
	    StringBuilder sb=new StringBuilder();
	    
	    for (int i = 20; i < 48; i++) {
			sb.append(((char)i));
		}
	    
	    String text = sb.toString();
	    System.out.println("string length="+text.length());
	    System.out.println("byte [] length="+text.getBytes().length);
	    
	    BigInteger p = (ElGamalAsymKeyFactory.create(false)).getP();
        int bitSize = p.bitLength();
        System.out.println("bitSize length="+bitSize);
        
		User user = new User();
		user.setNick("radoua");
		Hasher hasher = HasherFactory.createDefaultHasher();
		user.setSalt(HasherFactory.generateSalt());
		hasher.setSalt(user.getSalt());
		user.setPasswordHash(hasher.getHash(password.getBytes()));
		user.setCreatedAt(new Date());
		user.setKey(ElGamalAsymKeyFactory.create(false));
		user.setId("IdRadouaUser");
		
		User user2 = new User();
		user2.setNick("abderrahim");
		Hasher hasher2 = HasherFactory.createDefaultHasher();
		user2.setSalt(HasherFactory.generateSalt());
		hasher2.setSalt(user2.getSalt());
		user2.setPasswordHash(hasher2.getHash(password2.getBytes()));
		user2.setCreatedAt(new Date());
		user2.setKey(ElGamalAsymKeyFactory.create(false));
		user2.setId("IdAbderrahimUser");
		
		Message message  = new Message();
		message.setSendingDate(new Date());
		message.setSender(user.getId(), user.getNick());
		message.setReceiver(user2.getId(), user2.getNick());
		message.setMessageContent(text);
		
		ElGamalEncrypter encrypter = EncrypterFactory.createElGamalEncrypter();
		
		encrypter.setKey(user2.getKey());
		
		ParserAnnotation parser = ParserFactory.createDefaultParser(message, user2);
		
		System.out.println("le text a crypté : " + message.getMessageContent());
		
		message = (Message) parser.parseAnnotation(ParserAction.CryptAction);
		
		System.out.println("le text crypté : " + message.getMessageContent());
		
		ParserAnnotation parser2 = ParserFactory.createDefaultParser(message, user2);
		
		message = (Message) parser2.parseAnnotation(ParserAction.DecryptAction);
		
		System.out.println("le text decrypté : " + message.getMessageContent());
		
		assertEquals(text, message.getMessageContent());
	}
	
	
	
	
	public void ignore(){
		
		String password = "123456";
		String password2 = "abcdef";
	    String text = "Ce message est crypté de radoua.abderrahim@gmail.com : { _ 0 1 2 3 4 5 6 7 8 9 - + * & é \" [] \\ / àç!è§ }";
	    
		User user = new User();
		user.setNick("radoua");
		Hasher hasher = HasherFactory.createDefaultHasher();
		user.setSalt(HasherFactory.generateSalt());
		hasher.setSalt(user.getSalt());
		user.setPasswordHash(hasher.getHash(password.getBytes()));
		user.setCreatedAt(new Date());
		user.setKey(ElGamalAsymKeyFactory.create(false));
		user.setId("IdRadouaUser");
		
		User user2 = new User();
		user2.setNick("abderrahim");
		Hasher hasher2 = HasherFactory.createDefaultHasher();
		user2.setSalt(HasherFactory.generateSalt());
		hasher2.setSalt(user2.getSalt());
		user2.setPasswordHash(hasher2.getHash(password2.getBytes()));
		user2.setCreatedAt(new Date());
		user2.setKey(ElGamalAsymKeyFactory.create(false));
		user2.setId("IdAbderrahimUser");
		
		Message message  = new Message();
		
		message.setSendingDate(new Date());
		
		message.setSender(user.getId(), user.getNick());
		
		message.setReceiver(user2.getId(), user2.getNick());
		
		message.setMessageContent(text);
		
		System.out.println("ce text vas etre crypté : "+message.getMessageContent());
		
		ElGamalEncrypter encrypter = EncrypterFactory.createElGamalEncrypter();
		
		encrypter.setKey(user2.getKey());
		
		/*
		byte [] crypted = encrypter.encrypt(message.getMessageContent().getBytes());
	    try {
			message.setMessageContent(new String(crypted, "ISO-8859-1"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	    */
		
		String encodedString = Base64.getEncoder().encodeToString(encrypter.encrypt(message.getMessageContent().getBytes()));
	    
		message.setMessageContent(encodedString);
		
	    System.out.println("le text crypté : " + message.getMessageContent());
		
		ElGamalEncrypter decrypter = EncrypterFactory.createElGamalEncrypter();
		
		decrypter.setKey(user2.getKey());
		/*
		byte[] decrypted = null;
		try {
			decrypted = decrypter.decrypt(message.getMessageContent().getBytes("ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
			
		byte [] decrypted = decrypter.decrypt(Base64.getDecoder().decode(message.getMessageContent()));
		
		message.setMessageContent(new String(decrypted));
		
		System.out.println("le text decrypté : " + message.getMessageContent());
		
		assertTrue("TestCrypter", message.getMessageContent().equals(text));
	}

}
