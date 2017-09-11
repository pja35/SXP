/**
 * 
 */
package controller.managers;

import static org.junit.Assert.*;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import crypt.api.encryption.Encrypter;
import crypt.api.signatures.Signer;
import crypt.factories.AsymKeyFactory;
import crypt.factories.EncrypterFactory;
import crypt.factories.HasherFactory;
import crypt.factories.SignerFactory;
import crypt.impl.signatures.ElGamalSignature;
import model.api.MessageSyncManager;
import model.api.UserSyncManager;
import model.entity.ElGamalKey;
import model.entity.ElGamalSignEntity;
import model.entity.Message;
import model.entity.User;
import model.entity.Message.ReceptionStatus;
import model.factory.SyncManagerFactory;
import model.manager.ManagerAdapter;
import util.TestInputGenerator;
import util.TestUtils;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CryptoMessageManagerDecoratorTest {
	private final static Logger log = LogManager.getLogger(CryptoMessageManagerDecoratorTest.class);
	
	//USER_1
	private static User user_1;
	//datat for attributes user_1
	private static Date dt_1 = TestInputGenerator.getTodayDate();
	private static String userName_1 = TestInputGenerator.getRandomUser(20); 
	private static String password_1 = TestInputGenerator.getRandomPwd();
	private static byte [] passwordBytes_1 = password_1.getBytes();
	private static byte [] salt_1;
	private static ElGamalKey keys_1;

	//USER_2
	private static User user_2;
	//datat for attributes user_1
	private static Date dt_2 = TestInputGenerator.getTodayDate();
	private static String userName_2 = TestInputGenerator.getRandomUser(20); 
	private static String password_2 = TestInputGenerator.getRandomPwd();
	private static byte [] passwordBytes_2 = password_2.getBytes();
	private static byte [] salt_2;
	private static ElGamalKey keys_2;
	
	//MESSAGE
	private static Message message;
	//datat for Message
	private static String messageContent = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(3, 600));
	private static Date dt_message = TestInputGenerator.getTodayDate();
	private static ReceptionStatus status;
	
	
	private static String dbname = TestInputGenerator.getRandomDbName();
	
	private UserSyncManager userSyncManager;
	private CryptoUserManagerDecorator usm_1;
	private CryptoUserManagerDecorator usm_2;
	
	private MessageSyncManager messageSyncManager;
	private CryptoMessageManagerDecorator msm;
	
	private static JsonTools<byte[][]> json;

	@Before
	public void initialize() throws Exception {
		
		userSyncManager = SyncManagerFactory.createUserSyncManager();
		ManagerAdapter<User> adapterUser = new ManagerAdapter<>(userSyncManager);
		
		usm_1 = new CryptoUserManagerDecorator(adapterUser, user_1);
		usm_2 = new CryptoUserManagerDecorator(adapterUser, user_2);
		
		messageSyncManager = SyncManagerFactory.createMessageSyncManager();
		ManagerAdapter<Message> adapterMessage = new ManagerAdapter<>(messageSyncManager);
		
		msm = new CryptoMessageManagerDecorator(adapterMessage,null,user_2,user_1);
	}
	
	@After
	public void after(){
		usm_1.close();
		msm.close();
	}
	
	@BeforeClass
	public static void setUp() throws Exception {
		System.getProperties().put("derby.system.home", "./" + dbname + "/");
		
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
		
		user_1 = new User();
		keys_1 = AsymKeyFactory.createElGamalAsymKey(false);
		salt_1 = HasherFactory.generateSalt();
		
		user_2 = new User();
		keys_2 = AsymKeyFactory.createElGamalAsymKey(false);
		salt_2 = HasherFactory.generateSalt();
		
		message = new Message();
		
		json = new JsonTools<>(new TypeReference<byte[][]>(){});
	}

	
	@AfterClass
	public static void tearDown() throws Exception {
		clean();
		System.getProperties().put("derby.system.home", "./.simpleDb/");
	}
	

	public static void clean() throws Exception {
		File db = new File(dbname);
		TestUtils.removeRecursively(db);		
	}

	@Test
	public final void test_A_PERSIST_function() {

		//prepare two User for message
		
		user_1.setNick(userName_1);
		user_1.setCreatedAt(dt_1);
		user_1.setPasswordHash(passwordBytes_1);
		user_1.setSalt(salt_1);
		user_1.setKey(keys_1);
		user_1.setSignature(new ElGamalSignEntity()); 
		
		assertTrue(usm_1.begin());
		assertTrue(usm_1.persist(user_1));
		assertTrue(usm_1.end());
		
		user_2.setNick(userName_2);
		user_2.setCreatedAt(dt_2);
		user_2.setPasswordHash(passwordBytes_2);
		user_2.setSalt(salt_2);
		user_2.setKey(keys_2);
		user_2.setSignature(new ElGamalSignEntity()); 
		
		assertTrue(usm_2.begin());
		assertTrue(usm_2.persist(user_2));
		assertTrue(usm_2.end());
		
		user_1 = userSyncManager.getUser(userName_1, password_1);
		assertNotNull(user_1);
		user_2 = userSyncManager.getUser(userName_2, password_2);
		assertNotNull(user_2);
		
		message.setMessageContent(messageContent);
		message.setSender(user_1.getId(), user_1.getNick());
		message.setReceiver(user_2.getId(), user_2.getNick()); // a message will be crypted by using user_2 public key, and it's the same to sign a message
		message.setSendingDate(dt_message);
		message.setPbkey(user_1.getKey().getPublicKey());
		message.setStatus(status);
		
		//it's not required because we sign a message before we persist it.
		//message.setSignature(new ElGamalSignEntity());  
		
		assertTrue(msm.begin());
		assertTrue(msm.persist(message));
		assertTrue(msm.contains(message));
		assertTrue(msm.end());
		assertFalse(msm.contains(message));	
	}
	
	@Test
	public void test_B_check_Signature_Message() throws UnsupportedEncodingException{
		
		Collection<Message> messages = messageSyncManager.findAll();
		
		assertTrue(messages.size() == 1);
		
		Message m = messages.iterator().next();
		
		assertTrue(m.getSendingDate().equals(dt_message));
		assertTrue(m.getSenderId().equals(user_1.getId()));
		assertTrue(m.getSenderName().equals(user_1.getNick()));
		assertTrue(m.getReceiverId().equals(user_2.getId()));
		assertTrue(m.getReceiverName().equals(user_2.getNick()));
		
		assertNotNull(m.getSignature());
		
		assertFalse(m.getMessageContent().equals(messageContent));
		
		StringBuilder sb = new StringBuilder();
		//signeWithFields={"sendingDate","senderId","senderName","receiverId","receiverName","pbkey","messageContent"}
		sb.append(dt_message);
		sb.append(user_1.getId());
		sb.append(userName_1);
		sb.append(user_2.getId());
		sb.append(userName_2);
		sb.append(user_1.getKey().getPublicKey());
		sb.append(messageContent);
		
		Signer<ElGamalSignature, ElGamalKey> signer = SignerFactory.createElGamalSigner();
		
		signer.setKey(user_1.getKey());
		
		ElGamalSignature signatureVerify = new ElGamalSignature(m.getSignature().getR(), m.getSignature().getS());
		
		assertTrue(signer.verify(sb.toString().getBytes(), signatureVerify));
	}
	
	@Test
	public void test_C_check_crypted_Content(){
		
		Collection<Message> messages = messageSyncManager.findAll();
		
		assertTrue(messages.size() == 1);
		
		Message m = messages.iterator().next();
		
		assertTrue(m.getSendingDate().equals(dt_message));
		assertTrue(m.getSenderId().equals(user_1.getId()));
		assertTrue(m.getReceiverId().equals(user_2.getId()));

		byte[][] content = json.toEntity(m.getMessageContent(), true);

		Encrypter<ElGamalKey> encrypter = EncrypterFactory.createElGamalEncrypter();
		
		encrypter.setKey(user_2.getKey());
		
		byte[] key = encrypter.decrypt(content[0]);
		
		Encrypter<byte[]> decrypter = EncrypterFactory.createSerpentEncrypter();
		
		decrypter.setKey(key);
		
		String messageDecrypted = new String(decrypter.decrypt(content[2]));
		
		assertTrue(messageContent.equals(messageDecrypted));
	}
	
	
	@Test
	public void test_Z_clean(){
	  
		Collection<Message> collectionMessages =  messageSyncManager.findAll();
	  
		messageSyncManager.begin();
		  
		for (Message message : collectionMessages) {
			assertTrue(messageSyncManager.remove(message));
		}
		  
		assertTrue(messageSyncManager.end());
		
		//clean users
		
		Collection<User> collections =  userSyncManager.findAll();
	  
		userSyncManager.begin();
	  
		for (User user : collections) {
			assertTrue(userSyncManager.remove(user));
		}
	  
		assertTrue(userSyncManager.end());
	}
	
}

