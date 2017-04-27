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

import crypt.api.hashs.Hasher;
import crypt.api.signatures.Signer;
import crypt.factories.AsymKeyFactory;
import crypt.factories.HasherFactory;
import crypt.factories.SignerFactory;
import crypt.impl.signatures.ElGamalSignature;
import model.api.UserSyncManager;
import model.entity.ElGamalKey;
import model.entity.ElGamalSignEntity;
import model.entity.User;
import model.factory.SyncManagerFactory;
import model.manager.ManagerAdapter;
import util.TestInputGenerator;
import util.TestUtils;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CryptoUserManagerDecoratorTest {
	private final static Logger log = LogManager.getLogger(CryptoUserManagerDecoratorTest.class);
	
	private static Date dt = TestInputGenerator.getTodayDate();
	
	private static String userName = TestInputGenerator.getRandomUser(20); 
	
	private static String password = TestInputGenerator.getRandomPwd();
	private static byte [] passwordBytes = password.getBytes();
	
	private static byte [] salt;
	
	private static ElGamalKey keys;
	
	private static ElGamalSignEntity signature;// = new ElGamalSignEntity();
	
	private static String dbname = TestInputGenerator.getRandomDbName();
	
	private static User user;
	
	private UserSyncManager userSyncManager;
	private CryptoUserManagerDecorator usm;

	@Before
	public void initialize() throws Exception {
		userSyncManager = SyncManagerFactory.createUserSyncManager();
		ManagerAdapter<User> adapter = new ManagerAdapter<>(userSyncManager);
		usm = new CryptoUserManagerDecorator(adapter, user);
		signature = new ElGamalSignEntity();
		signature.setR(TestInputGenerator.getRandomBigInteger(100));
		signature.setS(TestInputGenerator.getRandomBigInteger(100));
	}
	
	@After
	public void after(){
		usm.close();
	}
	
	@BeforeClass
	public static void setUp() throws Exception {
		System.getProperties().put("derby.system.home", "./" + dbname + "/");
		
		user = new User();
		keys = AsymKeyFactory.createElGamalAsymKey(false);
		salt = HasherFactory.generateSalt();
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
	public final void test_A_PERSIST_and_END_function() {		
		user.setNick(userName);
		user.setCreatedAt(dt);
		user.setPasswordHash(passwordBytes); // passwordBytes is not hashed for now, but he will be hashed in decorator.end() function  {decorator : CryptoUserManagerDecorator}
		user.setSalt(salt);
		user.setKey(keys);
		user.setSignature(signature); // if signatutre = null, persist will not work because contraint @NotNull
		assertTrue(usm.begin());
		assertTrue(usm.persist(user));
		assertTrue(usm.contains(user));
		assertTrue(usm.end());   // in end() function : user entity will be hashed and signed by using decorator pattern
		assertFalse(usm.contains(user));
	}
	
	@Test
	public void test_C_Hashed_Password_User() throws UnsupportedEncodingException{
		
		Hasher hasher = HasherFactory.createDefaultHasher();
		
		hasher.setSalt(salt);
		
		byte [] passwordBytesHashed = hasher.getHash(passwordBytes);
		
		user = userSyncManager.getUser(userName, password);
		
		assertNotNull(user);
		
		assertTrue(user.getNick().equals(userName));
		
		assertTrue(user.getCreatedAt().equals(dt));
		
		assertNotNull(user.getSignature());
		
		assertArrayEquals(user.getSalt(), salt);
		
		assertArrayEquals(user.getPasswordHash(), passwordBytesHashed);
	}
	
	
	@Test
	public void test_C_check_Signature_User() throws UnsupportedEncodingException{
		
		Signer<ElGamalSignature, ElGamalKey> signer = SignerFactory.createElGamalSigner();
		
		Hasher hasher = HasherFactory.createDefaultHasher();
		
		hasher.setSalt(salt);
		
		byte [] passwordBytesHashed = hasher.getHash(passwordBytes);
		
		StringBuilder sb = new StringBuilder();
		
		//CryptSigneAnnotation(signeWithFields={"nick","createdAt","passwordHash","salt"},checkByKey="keys")
		sb.append(userName);
		sb.append(dt);
		sb.append(new String(passwordBytesHashed, "UTF-8"));
		sb.append(new String(salt, "UTF-8"));
		
		signer.setKey(user.getKey());
		
		ElGamalSignature signatureVerify = new ElGamalSignature(user.getSignature().getR(), user.getSignature().getS());
		
		assertTrue(signer.verify(sb.toString().getBytes(), signatureVerify));
	}
	
	
	@Test
	public void test_Z_cleanUsers(){
		
	  Collection<User> collections =  userSyncManager.findAll();
	  
	  userSyncManager.begin();
	  
	  for (User user : collections) {
		  assertTrue(userSyncManager.remove(user));
	  }
	  
	  assertTrue(userSyncManager.end());
	}
	
}

