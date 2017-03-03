package model.validator;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

import java.util.Date;

import javax.validation.ConstraintViolation;

import model.validator.UserValidator;
import util.TestInputGenerator;
import model.entity.ElGamalKey;
import model.entity.User;
import model.factory.ValidatorFactory;

/**
 * UserValidator unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class UserValidatorTest {
	private final static Logger log = LogManager.getLogger(UserValidatorTest.class);
	@Rule public ExpectedException exception = ExpectedException.none();

	User user;
	String id;
	String nick;
	byte[] salt;
	byte[] passwordHash;
	Date createdDate;
	ElGamalKey keys;
	UserValidator validator;
	ValidatorFactory factory;

	@Before
	public void initialize(){
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
		factory = new ValidatorFactory();
		validator = factory.createUserValidator();
	}

	@Test
	public void badNickValidationTest(){
		user.setNick(null);
		validator.setEntity(user);
		assertFalse(validator.validate());
		for(ConstraintViolation<User> v : validator.getViolations()){
			log.debug(v.getMessage());
		}
		nick = TestInputGenerator.getRandomUser(2);
		user.setNick(nick);
		validator.setEntity(user);
		assertFalse(validator.validate());
		for(ConstraintViolation<User> v : validator.getViolations()){
			log.debug(v.getMessage());
		}
		nick = TestInputGenerator.getRandomUser(65);
		user.setNick(nick);
		validator.setEntity(user);
		assertFalse(validator.validate());
		for(ConstraintViolation<User> v : validator.getViolations()){
			log.debug(v.getMessage());
		}
	}
	
	@Test
	public void badSaltValidationTest(){
		user.setSalt(null);
		validator.setEntity(user);
		assertFalse(validator.validate());
		for(ConstraintViolation<User> v : validator.getViolations()){
			log.debug(v.getMessage());
		}
	}
	
	@Test
	public void badCreatedAtValidationTest(){
		user.setCreatedAt(null);
		validator.setEntity(user);
		assertFalse(validator.validate());
		for(ConstraintViolation<User> v : validator.getViolations()){
			log.debug(v.getMessage());
		}
	}
	
	@Test
	public void badPasswordHashValidationTest(){
		user.setPasswordHash(null);
		validator.setEntity(user);
		assertFalse(validator.validate());
		for(ConstraintViolation<User> v : validator.getViolations()){
			log.debug(v.getMessage());
		}
	}
	
	@Test
	public void badKeyValidationTest(){
		user.setKey(null);
		validator.setEntity(user);
		assertFalse(validator.validate());
		for(ConstraintViolation<User> v : validator.getViolations()){
			log.debug(v.getMessage());
		}
	}
	
	@Test
	public void noEntityTest(){
		assertFalse(validator.validate());
	}
	
	@Test
	public void goodValidationTest(){
		validator.setEntity(user);
		assertTrue(validator.validate());
	}
}
