package model.entity;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.annotations.UuidGenerator;
import org.junit.Before;
import org.junit.Test;

import util.TestInputGenerator;

/**
 * User unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class UserTest {
	User user;
	String id;
	String nick;
	byte[] salt;
	byte[] passwordHash;
	Date createdDate;
	ElGamalKey keys;

	@Before
	public void instantiate(){
		user = new User();
		id = TestInputGenerator.getRandomUser();
		nick = TestInputGenerator.getRandomUser(TestInputGenerator.getRandomInt(3, 65));
		salt = TestInputGenerator.getRandomBytes(20);
		createdDate = TestInputGenerator.getTodayDate();
		passwordHash = TestInputGenerator.getRandomBytes(20);
		keys = new ElGamalKey();
		keys.setG(TestInputGenerator.getRandomBigInteger(100));
		keys.setP(TestInputGenerator.getRandomBigInteger(100));
		keys.setPrivateKey(TestInputGenerator.getRandomBigInteger(100));
		keys.setPublicKey(TestInputGenerator.getRandomBigInteger(100));
		user.setId(id);
		user.setNick(nick);
		user.setSalt(salt);
		user.setCreatedAt(createdDate);
		user.setPasswordHash(passwordHash);
		user.setKey(keys);
	}

	@Test
	public void gettersTest() {
		assertTrue(user.getId().equals(id));		
		assertTrue(user.getNick().equals(nick));		
		assertTrue(user.getCreatedAt().equals(createdDate));	
		assertTrue(user.getSalt().equals(salt));
		assertTrue(user.getPasswordHash().equals(passwordHash));				
		assertTrue(user.getKey().equals(keys));
	}

	@Test
	public void fieldsAnnotationsTest(){
		//Id
		try{
			Field idField = user.getClass().getDeclaredField("id");
			assertTrue(idField.getAnnotation(XmlElement.class).name().equals("id"));
			assertTrue(idField.getAnnotation(UuidGenerator.class).name().equals("uuid"));
			assertTrue(idField.getAnnotation(Id.class) != null);
			assertTrue(idField.getAnnotation(GeneratedValue.class).generator().equals("uuid"));
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		//nick
		try{
			Field nickField = user.getClass().getDeclaredField("nick");
			assertTrue(nickField.getAnnotation(XmlElement.class).name().equals("nick"));
			assertTrue(nickField.getAnnotation(Size.class).min() == 3);
			assertTrue(nickField.getAnnotation(Size.class).max() == 64);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		//createdAt
		try{
			Field creatField = user.getClass().getDeclaredField("createdAt");
			assertTrue(creatField.getAnnotation(XmlElement.class).name().equals("createdAt"));
			assertTrue(creatField.getAnnotation(NotNull.class) != null);
			assertTrue(creatField.getAnnotation(Temporal.class).value().compareTo(TemporalType.TIME) == 0);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		//salt
		try{
			Field saltField = user.getClass().getDeclaredField("salt");
			assertTrue(saltField.getAnnotation(XmlElement.class).name().equals("salt"));
			assertTrue(saltField.getAnnotation(NotNull.class) != null);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		//passwordHash
		try{
			Field pwdhashField = user.getClass().getDeclaredField("passwordHash");
			assertTrue(pwdhashField.getAnnotation(XmlElement.class).name().equals("passwordHash"));
			assertTrue(pwdhashField.getAnnotation(NotNull.class) != null);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		//key
		try{
			Field keysField = user.getClass().getDeclaredField("keys");
			assertTrue(keysField.getAnnotation(XmlElement.class).name().equals("keys"));
			assertTrue(keysField.getAnnotation(NotNull.class) != null);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void classAnnotationsTest(){
		assertTrue(user.getClass().getAnnotation(Entity.class) != null);
		assertTrue(user.getClass().getAnnotation(XmlRootElement.class) != null);
		assertTrue(user.getClass().getAnnotation(Table.class).name().equals("\"User\""));
	}
}
