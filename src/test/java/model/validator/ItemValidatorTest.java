package model.validator;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import crypt.api.signatures.Signer;
import crypt.factories.SignerFactory;
import model.entity.ElGamalSignEntity;
import model.entity.Item;
import model.factory.ValidatorFactory;
import util.TestInputGenerator;

/**
 * ItemValidator unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class ItemValidatorTest {
	@Rule public ExpectedException exception = ExpectedException.none();
	
	Item item;
	String title;
	String description;
	Date date;
	BigInteger publicKey;
	String username;
	String userid;
	ItemValidator validator;
	ValidatorFactory factory;

	ElGamalSignEntity signature;

	@SuppressWarnings("rawtypes")
	Signer signer;
	
	@Before
	public void initialize(){
		item = new Item();
		title = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(3, 256));
		description = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(3, 2001));
		date = TestInputGenerator.getTodayDate();
		publicKey = TestInputGenerator.getRandomBigInteger(TestInputGenerator.getRandomInt(3, 256));
		username = TestInputGenerator.getRandomUser(100); 
		userid = TestInputGenerator.getRandomUser();
		signature = new ElGamalSignEntity();
		signature.setR(TestInputGenerator.getRandomBigInteger(100));
		signature.setS(TestInputGenerator.getRandomBigInteger(100));
		
		item.setTitle(title);
		item.setDescription(description);
		item.setCreatedAt(date);
		item.setPbkey(publicKey);
		item.setUsername(username);
		item.setUserid(userid);
		item.setSignature(signature);
		factory = new ValidatorFactory();
		validator = ValidatorFactory.createItemValidator();
		signer = SignerFactory.createDefaultSigner();
	}
	
	@Test
	public void badTitleValidationTest(){
		item.setTitle(null);
		validator.setEntity(item);
		assertFalse(validator.validate());
		title = TestInputGenerator.getRandomIpsumString(2);
		item.setTitle(title);
		validator.setEntity(item);
		assertFalse(validator.validate());
		title = TestInputGenerator.getRandomIpsumString(256);
		item.setTitle(title);
		validator.setEntity(item);
		assertFalse(validator.validate());
	}
	
	@Test
	public void badDescriptionValidationTest(){
		item.setDescription(null);
		validator.setEntity(item);
		assertFalse(validator.validate());
		description = TestInputGenerator.getRandomIpsumString(2);
		item.setDescription(description);
		validator.setEntity(item);
		assertFalse(validator.validate());
		description = TestInputGenerator.getRandomIpsumString(2001);
		item.setDescription(description);
		validator.setEntity(item);
		assertFalse(validator.validate());
	}
	
	@Test
	public void badDateValidationTest(){
		item.setCreatedAt(null);
		validator.setEntity(item);
		assertFalse(validator.validate());
	}
	
	@Test
	public void badpbKeyValidationTest(){
		item.setPbkey(null);
		validator.setEntity(item);
		assertFalse(validator.validate());
	}
	
	@Test
	public void badUsernameValidationTest(){
		item.setUsername(null);
		validator.setEntity(item);
		assertFalse(validator.validate());
		username = TestInputGenerator.getRandomUser(1);
		item.setUsername(username);
		validator.setEntity(item);
		assertFalse(validator.validate());
		username = TestInputGenerator.getRandomUser(256);
		item.setUsername(username);
		validator.setEntity(item);
		assertFalse(validator.validate());
	}
	
	@Test
	public void badUseridValidationTest(){
		item.setUserid(null);
		validator.setEntity(item);
		assertFalse(validator.validate());
	}
	
//	@Test
//	public void signerExceptionTest() throws RuntimeException {
//		assertFalse(validator.validate());
//		validator.setEntity(item);
//		exception.expect(RuntimeException.class);
//		exception.expectMessage("no signer were setteld");
//		validator.validate();
//	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void signatureValidationTest(){
		validator.setEntity(item);
		validator.setSigner(signer);
		assertTrue(validator.validate());
	}
}
