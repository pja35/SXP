/**
 * 
 */
package model.syncManager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.math.BigInteger;
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

import model.api.ItemSyncManager;
import model.entity.ElGamalSignEntity;
import model.entity.Item;
import model.factory.SyncManagerFactory;
import util.TestInputGenerator;
import util.TestUtils;

/**
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ItemSyncManagerImplTest {
	private final static Logger log = LogManager.getLogger(ItemSyncManagerImplTest.class);
	
	private static String title = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(3, 256));
	private static String description = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(3, 256));
	private static Date dt = TestInputGenerator.getTodayDate();
	private static BigInteger pbkey = TestInputGenerator.getRandomNotNullBigInteger(256);
	private static String userName = TestInputGenerator.getRandomUser(100); 
	private static String userId = TestInputGenerator.getRandomUser();
	private static String dbname = TestInputGenerator.getRandomDbName();
	private static String Id;
	private static ElGamalSignEntity signature = new ElGamalSignEntity();
	
	
	
	private ItemSyncManager ism;
	private Item it;	
	

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void initialize() throws Exception {
		ism = SyncManagerFactory.createItemSyncManager();
		it = new Item();
		signature.setR(TestInputGenerator.getRandomBigInteger(100));
		signature.setS(TestInputGenerator.getRandomBigInteger(100));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		System.getProperties().put("derby.system.home", "./" + dbname + "/");
	}

	/**
	 * @throws java.lang.Exception
	 */
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
	public final void testA() {
		assertTrue(ism.begin());
		assertFalse(ism.persist(it)); 
		// javax.validation.ConstraintViolationException: Bean Validation constraint(s) violated while executing Automatic Bean Validation on callback event:'prePersist'. Please refer to embedded ConstraintViolations for details.
		assertFalse(ism.end());
	}

	@Test
	public final void testB() {		
		it.setTitle(title);
		it.setDescription(description);
		it.setCreatedAt(dt);
		it.setPbkey(pbkey);
		it.setUsername(userName);
		it.setUserid(userId);
		it.setSignature(signature); // add to resolve validator not null for signature attribute
		assertTrue(ism.begin());
		assertTrue(ism.persist(it));
		log.debug(dumpWL(ism));
		assertTrue(ism.contains(it));
		assertTrue(ism.end());
		assertFalse(ism.contains(it));	
	}
	
	@Test
	public final void testC() {
		Collection<Item> items = ism.findAll();
		int x = 0;
		for(Item ui : items){
			Id = ui.getId();
			log.debug(x + " : " + ui.getId() + " : " + ui.getDescription());
//			assertTrue(ui.getDescription().equals(description));
//			assertTrue(ui.getTitle().equals(title));
			x++;
		}
		log.debug(dumpWL(ism));
		assertTrue(x == 1);		
		Item ui = ism.findOneById(Id);
		assertTrue(ui.getDescription().equals(description));
		assertTrue(ui.getTitle().equals(title));
	}
	
	@Test
	public final void testD() {
		title = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(3, 256));
		description = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(3, 256));
		it.setTitle(title);
		it.setDescription(description);
		it.setCreatedAt(dt);
		it.setPbkey(pbkey);
		it.setUsername(userName);
		it.setUserid(userId);
		it.setSignature(signature);
		assertTrue(ism.begin());
		assertTrue(ism.persist(it));
		log.debug(dumpWL(ism));
		assertTrue(ism.check());
		it.setTitle("");
		assertFalse(ism.check());
		it.setTitle(title);
		assertTrue(ism.contains(it));
		assertTrue(ism.end());
	}
	
	
	public static String dumpWL(ItemSyncManager ism){
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

