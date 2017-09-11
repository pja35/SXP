package crypt.utils;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import crypt.api.annotation.ParserAction;
import crypt.api.annotation.ParserAnnotation;
import crypt.api.hashs.Hasher;
import crypt.api.signatures.Signer;
import crypt.factories.AsymKeyFactory;
import crypt.factories.HasherFactory;
import crypt.factories.ParserFactory;
import crypt.factories.SignerFactory;
import crypt.impl.signatures.ElGamalSignature;
import model.entity.ElGamalKey;
import model.entity.ElGamalSignEntity;
import model.entity.Item;
import model.entity.Message;
import model.entity.User;
import model.entity.Message.ReceptionStatus;
import util.TestInputGenerator;
//import org.apache.commons.lang.SerializationUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CryptoParserTest {
	
	private static String title = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(3, 256));
	private static String description = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(3, 256));
	private static String messageContent = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(3, 1024));
	
	private static Date dtItemCreated = TestInputGenerator.getTodayDate();
	private static Date dtUser_1Created = TestInputGenerator.getTodayDate();
	private static Date dtUser_2Created = TestInputGenerator.getTodayDate();
	private static Date dtMessageCreated = TestInputGenerator.getTodayDate();
	
	private static String userName_1 = TestInputGenerator.getRandomUser();
	private static String userName_2 = TestInputGenerator.getRandomUser();
	
	private static String userId_1 = TestInputGenerator.getRandomUser(100);
	private static String userId_2 = TestInputGenerator.getRandomUser(100);
	
	private static byte[] salt_1 = TestInputGenerator.getRandomBytes(20);
	private static byte[] salt_2 = TestInputGenerator.getRandomBytes(20);
	
	private static String passwordString_1 = TestInputGenerator.getRandomUser();
	private static String passwordString_2 = TestInputGenerator.getRandomUser();
	
	private static byte[] passwordHash_1;
	private static byte[] passwordHash_2;
	
	private static ElGamalKey keys_1;// = new ElGamalKey();
	private static ElGamalKey keys_2;// = new ElGamalKey();
	
	private static ElGamalSignEntity signEntity = new ElGamalSignEntity();
	
	private static ReceptionStatus status;
	
	private static User user_1;
	private static User user_2;
	private static Item item;
	private static Message message;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		int s = TestInputGenerator.getRandomInt(0, 3);
		switch(s){
		case 0:
			status = ReceptionStatus.DRAFT;
			break;
		case 1:
			status = ReceptionStatus.RECEIVED;
			break;
		default:
			status = ReceptionStatus.SENT;
			break;				
		}
		
		keys_1 = AsymKeyFactory.createElGamalAsymKey(false);
		keys_2 = AsymKeyFactory.createElGamalAsymKey(false);
		
		signEntity.setR(TestInputGenerator.getRandomBigInteger(100));
		signEntity.setS(TestInputGenerator.getRandomBigInteger(100));
		
		passwordHash_1 = passwordString_1.getBytes();
		passwordHash_2 = passwordString_2.getBytes();
	}


	@Before
	public void setUp() throws Exception {
		user_1 = new User();
		user_2 = new User();
		item = new Item();
		message = new Message();
		
		user_1.setId(userId_1);
		user_1.setCreatedAt(dtUser_1Created);
		user_1.setNick(userName_1);
		user_1.setPasswordHash(passwordString_1.getBytes());
		user_1.setKey(keys_1);
		user_1.setSalt(salt_1);
		
		user_2.setId(userId_2);
		user_2.setCreatedAt(dtUser_2Created);
		user_2.setNick(userName_2);
		user_2.setPasswordHash(passwordHash_2);
		user_2.setKey(keys_2);
		user_2.setSalt(salt_2);
		
		item.setTitle(title);
		item.setDescription(description);
		item.setPbkey(user_1.getKey().getPublicKey());
		item.setCreatedAt(dtItemCreated);
		item.setUsername(user_1.getNick());
		item.setSignature(new ElGamalSignEntity());
		item.setUserid(user_1.getId());
		
		message.setMessageContent(messageContent);
		message.setSender(userId_2, userName_2);
		message.setReceiver(userId_1, userName_1);
		message.setPbkey(user_1.getKey().getPublicKey());
		message.setStatus(status);
		message.setSendingDate(dtMessageCreated);
	}	

	@Test
	public void test_A_HashUser(){
		
		ParserAnnotation parser = ParserFactory.createDefaultParser(user_1, user_1.getKey());
	
		Hasher hasher_1 = HasherFactory.createDefaultHasher();
		
		assertArrayEquals(user_1.getPasswordHash(), passwordHash_1); // password not hashed
		
		user_1 = (User) parser.parseAnnotation(ParserAction.HasherAction);
		
		hasher_1.setSalt(salt_1);
		
		assertFalse(Arrays.toString(user_1.getPasswordHash()).equals(Arrays.toString(passwordHash_1)));
		
		assertArrayEquals(hasher_1.getHash(passwordHash_1),user_1.getPasswordHash());
	}
	
	
	@Test
	public void test_B_SignuatureUser() throws UnsupportedEncodingException{
		
		ParserAnnotation<User> parser = ParserFactory.createDefaultParser(user_1, user_1.getKey());
		
		user_1 = parser.parseAnnotation(ParserAction.SigneAction);
		
		Signer<ElGamalSignature, ElGamalKey> signer = SignerFactory.createElGamalSigner();
		
		ElGamalSignature signatureVerify = new ElGamalSignature(user_1.getSignature().getR(), user_1.getSignature().getS());
		
		StringBuilder sbUser = new StringBuilder();
		
		StringBuilder sb = new StringBuilder();
		//"nick","createdAt","passwordHash","salt"
		sb.append(userName_1);
		sb.append(dtUser_1Created);
		sb.append(new String(user_1.getPasswordHash(), "UTF-8"));
		sb.append(new String(salt_1, "UTF-8"));
		
		
		assertNotNull(user_1.getSignature());
		assertNull(user_2.getSignature());
		
		sbUser.append(user_1.getNick());
		sbUser.append(user_1.getCreatedAt());
		sbUser.append(new String(user_1.getPasswordHash(), "UTF-8"));
		sbUser.append(new String(user_1.getSalt(), "UTF-8"));
		
		signer.setKey(keys_1);
		
		assertTrue(signer.verify(sbUser.toString().getBytes(), signatureVerify));
	}
	
	@Test
	public void test_C_signatureItem(){
		
		Signer<ElGamalSignature, ElGamalKey> signer = SignerFactory.createElGamalSigner();
		StringBuilder sb = new StringBuilder();
		
		ParserAnnotation parser = ParserFactory.createDefaultParser(item, user_1.getKey());
		item = (Item) parser.parseAnnotation(ParserAction.SigneAction);
		
		//"title","description","createdAt","username","userid","pbkey"
		sb.append(title);
		sb.append(description);
		sb.append(dtItemCreated);
		sb.append(userName_1);
		sb.append(userId_1);
		sb.append(user_1.getKey().getPublicKey());
		
		signer.setKey(keys_1);
		
		ElGamalSignature signatureVerify = new ElGamalSignature(item.getSignature().getR(), item.getSignature().getS());
		assertTrue(signer.verify(sb.toString().getBytes(), signatureVerify));
	}
	
	
	@Test
	public void test_D_signMessage(){
		Signer<ElGamalSignature, ElGamalKey> signer = SignerFactory.createElGamalSigner();
		StringBuilder sb = new StringBuilder();
		
		ParserAnnotation parser = ParserFactory.createDefaultParser(message, user_1.getKey());
		message = (Message) parser.parseAnnotation(ParserAction.SigneAction);
		
		//"sendingDate","senderId","senderName","receiverId","receiverName","pbkey","messageContent"
		sb.append(dtMessageCreated);
		sb.append(userId_2);
		sb.append(userName_2);
		sb.append(userId_1);
		sb.append(userName_1);
		sb.append(user_1.getKey().getPublicKey());
		sb.append(messageContent);
		
		signer.setKey(keys_1);
		
		ElGamalSignature signatureVerify = new ElGamalSignature(message.getSignature().getR(), message.getSignature().getS());
		assertTrue(signer.verify(sb.toString().getBytes(), signatureVerify));
	}
	
	@Test
	public void test_E_cryptAndDecryptMessage(){
		
		assertEquals(messageContent, message.getMessageContent()); // before crypt message content 
		
		ParserAnnotation parser = ParserFactory.createDefaultParser(message, user_1.getKey());
		
		message = (Message) parser.parseAnnotation(ParserAction.CryptAction);
		
		assertNotEquals(messageContent, message.getMessageContent()); // after crypt
		
		message = (Message) parser.parseAnnotation(ParserAction.DecryptAction);
		
		assertEquals(messageContent, message.getMessageContent()); // after decrypt
	}
	
}
