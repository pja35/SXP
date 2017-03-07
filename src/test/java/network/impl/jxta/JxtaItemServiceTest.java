package network.impl.jxta;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.jxta.endpoint.Message;
import network.api.Messages;
import network.api.service.InvalidServiceException;
import network.factories.PeerFactory;
import util.TestInputGenerator;
import util.TestUtils;

/**
 * JxtaItemSerice unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class JxtaItemServiceTest {
	private final static Logger log = LogManager.getLogger(JxtaItemServiceTest.class);
	@Rule public ExpectedException exception = ExpectedException.none();

	static private String cache = TestInputGenerator.getRandomCacheName();
	static private JxtaPeer jxtaPeer;

	@BeforeClass
	public static void setUpClass() {
		log.debug("**************** Starting test");
		TestUtils.removeRecursively(new File(cache));
		jxtaPeer = PeerFactory.createJxtaPeer();
		try {
			jxtaPeer.start(cache, 9800);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
	}

	@AfterClass
	public static void tearDownClass() {
		jxtaPeer.stop();
		TestUtils.removeRecursively(new File(cache));
	}


	JxtaItemService jxtaItemService;

	@Before
	public void initialize(){
		jxtaItemService = new JxtaItemService();		
	}

	@Test
	public void addServiceToPeer(){
		try {
			jxtaPeer.addService(jxtaItemService);
		} catch (InvalidServiceException e) {
			fail();
			log.debug(e.getMessage());
		}
		assertTrue(jxtaPeer.getService("items").equals(jxtaItemService));
	}

	@Test
	public void badInitTest() throws RuntimeException, InvalidServiceException{
		exception.expect(RuntimeException.class);
		exception.expectMessage("Need a Jxta Peer to run a Jxta service");	
		jxtaItemService.initAndStart(null);
	}

	@Test
	public void getNameTest() {
		assertTrue(jxtaItemService.getName().equals("items"));
	}

	@Test
	public void messageTest(){
		Messages messages = jxtaItemService.toMessages(new Message());
		assertNull(messages.getWho());
		String[] names = messages.getNames();
		// Message exists ...
		assertTrue(names.length == 1);
		// with null name.
		assertNull(names[0]);
	}
}
