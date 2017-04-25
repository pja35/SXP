package model.syncManager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import model.api.UserSyncManager;
import model.entity.ElGamalKey;
import model.entity.ElGamalSignEntity;
import model.entity.User;
import model.factory.SyncManagerFactory;
import util.TestInputGenerator;
import util.TestUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserSyncManagerImplTest {
	private final static Logger log = LogManager.getLogger(UserSyncManagerImplTest.class);
	
	private static String id;
	private static String nick = TestInputGenerator.getRandomUser(TestInputGenerator.getRandomInt(3, 65));
	private static byte[] salt = TestInputGenerator.getRandomBytes(20);
	private static byte[] passwordHash = TestInputGenerator.getRandomBytes(20);
	private static Date createdDate = TestInputGenerator.getTodayDate();
	private static ElGamalKey keys = new ElGamalKey();
	private static String dbname = TestInputGenerator.getRandomDbName();
	
	private UserSyncManager usm;
	private User user;
	private static ElGamalSignEntity signature = new ElGamalSignEntity();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Properties p = System.getProperties();
		p.put("derby.system.home", "./" + dbname + "/");
		keys.setG(TestInputGenerator.getRandomBigInteger(100));
		keys.setP(TestInputGenerator.getRandomBigInteger(100));
		keys.setPrivateKey(TestInputGenerator.getRandomBigInteger(100));
		keys.setPublicKey(TestInputGenerator.getRandomBigInteger(100));
		signature.setR(TestInputGenerator.getRandomBigInteger(100));
		signature.setS(TestInputGenerator.getRandomBigInteger(100));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		clean();
		System.getProperties().put("derby.system.home", "./.simpleDb/");
	}

	@Before
	public void setUp() throws Exception {
		user = new User();
		usm = SyncManagerFactory.createUserSyncManager();
	}

	@After
	public void tearDown() throws Exception {
		usm.close();
	}
	
	public static void clean() throws Exception {
		File db = new File(dbname);
		TestUtils.removeRecursively(db);
	}

	@Test
	public final void testA() {
		assertTrue(usm.begin());
		assertFalse(usm.persist(user)); 
		// javax.validation.ConstraintViolationException: Bean Validation constraint(s) violated while executing Automatic Bean Validation on callback event:'prePersist'. Please refer to embedded ConstraintViolations for details.
		assertFalse(usm.end());
	}

	@Test
	public final void testB() {
		user.setCreatedAt(createdDate);
		user.setNick(nick);
		user.setPasswordHash(passwordHash);
		user.setKey(keys);
		user.setSalt(salt);
		user.setSignature(signature);
		assertTrue(usm.begin());
		assertTrue(usm.persist(user));
		log.debug(dumpWL(usm));
		assertTrue(usm.contains(user));
		assertTrue(usm.end());
		assertFalse(usm.contains(user));	
	}
	
	@Test
	public final void testC() {
		Collection<User> users = usm.findAll();
		int x = 0;
		for(User u : users){
			id = u.getId();
			log.debug(x + " : " + u.getId() + " : " + u.getNick());
			assertTrue(u.getId().equals(id));		
			assertTrue(u.getNick().equals(nick));		
			assertTrue(u.getCreatedAt().equals(createdDate));	
			assertTrue(u.getSalt().equals(salt));
			assertTrue(u.getPasswordHash().equals(passwordHash));
			//assertTrue(u.getKey().equals(keys));
			x++;
		}
		assertTrue(x == 1);		
		User u = usm.findOneById(id);
		assertTrue(u.getId().equals(id));		
		assertTrue(u.getNick().equals(nick));		
		assertTrue(u.getCreatedAt().equals(createdDate));	
		assertTrue(u.getSalt().equals(salt));
		assertTrue(u.getPasswordHash().equals(passwordHash));				
		//assertTrue(u.getKey().equals(keys));
	}
	
	
	@Test
	public final void testD() {
		nick = TestInputGenerator.getRandomUser(TestInputGenerator.getRandomInt(3, 65));
		user.setCreatedAt(createdDate);
		user.setNick(nick);
		user.setPasswordHash(passwordHash);
		user.setKey(keys);
		user.setSalt(salt);
		user.setSignature(signature);
		assertTrue(usm.begin());
		assertTrue(usm.persist(user));
		log.debug(dumpWL(usm));
		assertTrue(usm.check());
		user.setNick("");
		assertFalse(usm.check());
		user.setNick(nick);
		assertTrue(usm.contains(user));
		assertTrue(usm.end());
	}
	
	public static String dumpWL(UserSyncManager usm){
		StringBuffer buff = new StringBuffer();
		Set<User> uss = (Set<User>) usm.watchlist();
		buff.append("\n**** Watchlist ****" + "\n");
		for (User u : uss){
			buff.append("------------" + "\n");
			buff.append(u.getId() + "\n");
			buff.append(u.getNick() + "\n");
			buff.append(u.getCreatedAt() + "\n");
			buff.append(TestInputGenerator.byteToString(u.getPasswordHash()) + "\n");
		}
		buff.append("******************" + "\n");
		return buff.toString();
	}

}
