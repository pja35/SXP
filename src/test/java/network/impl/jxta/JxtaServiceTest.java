package network.impl.jxta;


import net.jxta.endpoint.Message;

import network.api.Messages;
import network.api.service.InvalidServiceException;
import network.factories.PeerFactory;
import network.impl.jxta.JxtaService;
import network.impl.messages.RequestItemMessageTest;
import util.TestInputGenerator;
import util.TestUtils;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;


/**
 * JxtaService unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class JxtaServiceTest {
	private final static Logger log = LogManager.getLogger(JxtaServiceTest.class);
	@Rule public ExpectedException exception = ExpectedException.none();

	static private String cache = TestInputGenerator.getRandomCacheName();
	static private JxtaPeer jxtaPeer;

	@BeforeClass
	public static void setUpClass() {
		TestUtils.removeRecursively(new File(cache));
		jxtaPeer = PeerFactory.createJxtaPeer();
		try {
			jxtaPeer.start(cache, 9800);
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
	}

	@AfterClass
	public static void tearDownClass() {
		jxtaPeer.stop();
		TestUtils.removeRecursively(new File(cache));
	}


	JxtaService jxtaService;

	@Before
	public void initialize(){
		jxtaService = new JxtaService();		
	}

	@Test
	public void addServiceToPeer() throws InvalidServiceException{
		exception.expect(InvalidServiceException.class);
		exception.expectMessage("Service name is empty");	
		jxtaPeer.addService(jxtaService);
//		//The service has not been added and we can check it with~:
//		assertNull(jxtaService.pg);
	}

	@Test
	public void badInitTest() throws InvalidServiceException{
		exception.expect(RuntimeException.class);
		exception.expectMessage("Need a Jxta Peer to run a Jxta service");	
		jxtaService.initAndStart(null);
	}

	@Test
	public void noNameTest() {
		assertNull(jxtaService.getName());
	}

	@Test
	public void messageTest(){
		Messages messages = jxtaService.toMessages(new Message());
		assertNull(messages.getWho());
		String[] names = messages.getNames();
		// Message exists ...
		assertTrue(names.length == 1);
		// with null name.
		assertNull(names[0]);
	}
}

