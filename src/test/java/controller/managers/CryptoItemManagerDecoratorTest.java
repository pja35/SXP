/**
 * 
 */
package controller.managers;

import static org.junit.Assert.*;


import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import crypt.api.signatures.Signer;
import crypt.factories.AsymKeyFactory;
import crypt.factories.SignerFactory;
import crypt.impl.signatures.ElGamalSignature;
import model.api.ItemSyncManager;
import model.api.ManagerListener;
import model.entity.ElGamalKey;
import model.entity.ElGamalSignEntity;
import model.entity.Item;
import model.entity.User;
import model.factory.SyncManagerFactory;
import model.manager.ManagerAdapter;
import util.TestInputGenerator;
import util.TestUtils;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CryptoItemManagerDecoratorTest {
	private final static Logger log = LogManager.getLogger(CryptoItemManagerDecoratorTest.class);
	
	private static String title = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(5, 25));
	private static String description = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(5, 256));
	private static Date dt = TestInputGenerator.getTodayDate();
	private static String userName = TestInputGenerator.getRandomUser(20); 
	private static String userId = TestInputGenerator.getRandomUser(100);
	
	private static ElGamalKey keys = AsymKeyFactory.createElGamalAsymKey(false);
	private static BigInteger pbkey = keys.getPublicKey();
	
	private static ElGamalSignEntity signature;// = new ElGamalSignEntity();
	
	private static String dbname = TestInputGenerator.getRandomDbName();
	private static String Id;
	
	private static User user;
	
	private ItemSyncManager itemSyncManager;
	private CryptoItemManagerDecorator ism;
	private static Item it;

	@Before
	public void initialize() throws Exception {
		
		itemSyncManager = SyncManagerFactory.createItemSyncManager();
		
		ManagerAdapter<Item> adapter = new ManagerAdapter<>(itemSyncManager);
		
		ism = new CryptoItemManagerDecorator(adapter, user);
		
		signature = new ElGamalSignEntity();
		signature.setR(TestInputGenerator.getRandomBigInteger(100));
		signature.setS(TestInputGenerator.getRandomBigInteger(100));
	}

	@BeforeClass
	public static void setUp() throws Exception {
		System.getProperties().put("derby.system.home", "./" + dbname + "/");
		
		user = new User();
		it = new Item();
		
		// set a user before test
		user.setNick(userName);
		user.setId(userId);
		user.setKey(keys);
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
		
		it.setTitle(title);
		it.setDescription(description);
		it.setCreatedAt(dt);
		it.setPbkey(pbkey);
		it.setUsername(userName);
		it.setUserid(userId);
		it.setSignature(signature); // add 'signature' to resolve validator not null for signature attribute
		
		assertTrue(ism.begin());
		assertTrue(ism.persist(it));
		log.debug(dumpWL(ism));
		assertTrue(ism.contains(it));
		assertTrue(ism.end());
		assertFalse(ism.contains(it));	
	}
	
	@Test
	public void test_B_check_Signature_Item(){
		
		Signer<ElGamalSignature, ElGamalKey> signer = SignerFactory.createElGamalSigner();
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(title);
		sb.append(description);
		sb.append(dt);
		sb.append(userName);
		sb.append(userId);
		sb.append(user.getKey().getPublicKey());
		
		signer.setKey(user.getKey());
		
		ElGamalSignature signatureVerify = new ElGamalSignature(it.getSignature().getR(), it.getSignature().getS());
		
		assertTrue(signer.verify(sb.toString().getBytes(), signatureVerify));
	}
	
	@Test
	public void test_C_findAllByAttribute() {
		
		final ArrayList<Item> items = new ArrayList<>();
				
		ism.findAllByAttribute("title", title, new ManagerListener<Item>() {
			
			@Override
			public void notify(Collection<Item> results) {
				items.addAll(results);
			}
		});
		
		int x = 0;
		for(Item ui : items){
			Id = ui.getId();
			log.debug(x + " : " + ui.getId() + " : " + ui.getDescription());
			x++;
		}
		log.debug(dumpWL(ism));
		assertTrue(x == 1);		
		Item ui = itemSyncManager.findOneById(Id);
		assertTrue(ui.getDescription().equals(description));
		assertTrue(ui.getTitle().equals(title));
	}

	@Test
	public void test_D_findOneById() {
		
		ism.findOneByAttribute("id", it.getId(), new ManagerListener<Item>() {
			@Override
			public void notify(Collection<Item> results) {
				Item item = results.iterator().next();
				assertTrue(results.size() == 1);
				assertTrue(item.getTitle().equals(title));
				assertTrue(item.getDescription().equals(description));
				assertTrue(item.getCreatedAt().equals(dt));
				assertTrue(item.getUserid().equals(userId));
				assertTrue(item.getUsername().equals(userName));
				assertNotNull(item.getPbkey());
				assertNotNull(item.getSignature());
			}
		});
	}
	
	
	@Test
	public void test_E_findOneByAttribute() {
		
		ism.findOneByAttribute("title", title, new ManagerListener<Item>() {
			@Override
			public void notify(Collection<Item> results) {
				Item item = results.iterator().next();
				assertTrue(results.size() == 1);
				assertTrue(item.getTitle().equals(title));
				assertTrue(item.getDescription().equals(description));
				assertTrue(item.getCreatedAt().equals(dt));
				assertTrue(item.getUserid().equals(userId));
				assertTrue(item.getUsername().equals(userName));
				assertNotNull(item.getPbkey());
				assertNotNull(item.getSignature());
			}
		});
		
		ism.findOneByAttribute("title", "", new ManagerListener<Item>() {
			@Override
			public void notify(Collection<Item> results) {
				assertTrue(results.size() == 0);
			}
		});
	}
	
	@Test
	public void test_F_getItemWithBadSignature() {
		
		//find with CryptoItemManagerDecorator before editing Item with SyncManager
		ism.findOneByAttribute("title", title, new ManagerListener<Item>() {
			@Override
			public void notify(Collection<Item> results) {
				Item item = results.iterator().next();
				assertTrue(results.size() == 1);
				assertTrue(item.getTitle().equals(title));
				assertTrue(item.getDescription().equals(description));
			}
		});
		
		// edit Item description with Item SyncManager, to modifier item without updating signature
		Item i = itemSyncManager.findOneByAttribute("title", title);
		assertNotNull(i);
		assertTrue(i.getDescription().equals(description));
		
		//edit item
		itemSyncManager.begin();
		i.setDescription(description+"__ALTER DESCRIPTION__");
		itemSyncManager.end();
		
		//find with CryptoItemManagerDecorator
		ism.findOneByAttribute("title", title, new ManagerListener<Item>() {
			@Override
			public void notify(Collection<Item> results) {
				assertTrue(results.size() == 0);
			}
		});
	}
	
	
	@Test
	public void test_Z_cleanItems(){
		
	  Collection<Item> collections =  itemSyncManager.findAll();
	  
	  itemSyncManager.begin();
	  
	  for (Item item : collections) {
		  assertTrue(itemSyncManager.remove(item));
	  }
	  
	  assertTrue(itemSyncManager.end());
	}
	
	
	public static String dumpWL(CryptoItemManagerDecorator ism){
		StringBuffer buff = new StringBuffer();
		Set<Item> items = (Set<Item>) ism.watchlist();
		buff.append("\n**** Watchlist ****" + "\n");
		for (Item i : items){
			buff.append("------------" + "\n");
			buff.append(i.getDescription() + "\n");
			buff.append(i.getId() + "\n");
			buff.append(i.getTitle() + "\n");
			buff.append(i.getUserid() + "\n");
			buff.append(i.getUsername() + "\n");
			buff.append(i.getCreatedAt() + "\n");
			buff.append(i.getPbkey() + "\n");
		}
		buff.append("******************" + "\n");
		return buff.toString();
	}
}

