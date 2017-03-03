package network.impl.jxta;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.ExpectedException;

import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import util.TestInputGenerator;
import util.TestUtils;

/**
 * JxtaNode unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class JxtaNodeTest {
	private final static Logger log = LogManager.getLogger(JxtaNodeTest.class);
	@Rule public ExpectedException exception = ExpectedException.none();
	@Rule public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	static private JxtaNode node;
	static private String cache = TestInputGenerator.getRandomCacheName();
	static private String cache2 = TestInputGenerator.getRandomCacheName();
	static private String cache3 = TestInputGenerator.getRandomCacheName();

	@BeforeClass
	public static void setUpClass() {
		log.debug("**************** Starting test");
		TestUtils.removeRecursively(new File(cache));
		node = new JxtaNode();
		try {
			node.initialize(cache, "testNode", true);
			//node.start(9800);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
	}

	@AfterClass
	public static void tearDownClass() {
		TestUtils.removeRecursively(new File(cache));
		TestUtils.removeRecursively(new File(cache2));
		TestUtils.removeRecursively(new File(cache3));
		//node.stop();
	}

	@Test
	public void getNetworkManagerTest(){
		NetworkManager nwMger = node.getNetworkManager();
		assertTrue(nwMger.getInstanceName().equals("testNode"));
	}

	@Test
	public void isInitializedTest(){
		assertTrue(node.isInitialized());
	}

	@Test
	public void startUninitializedNodeExeceptionTest() throws PeerGroupException, RuntimeException, IOException{
		JxtaNode newNode = new JxtaNode(); 
		exception.expect(RuntimeException.class);
		exception.expectMessage("Node must be initalized before start call");	
		newNode.start(9801);
	}

	@Test
	public void startBadCacheNodeExitTest() throws PeerGroupException, RuntimeException, IOException{		
		File cacheDirectory = new File(cache2);
		TestUtils.removeRecursively(cacheDirectory);
		cacheDirectory.mkdirs();
		JxtaNode node2 = new JxtaNode();
		try {
			node2.initialize(cache2, "testNode2", true);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		TestUtils.removeRecursively(cacheDirectory);
		cacheDirectory.createNewFile();
		exception.expect(PeerGroupException.class);
		exception.expectMessage("error while creating main peer group");	
		node2.start(9800);
	}
	
	@Test
	public void startNodeTest() throws PeerGroupException, RuntimeException, IOException{
		node.start(9800);
		assertTrue(node.isStarted());
	 }
	
	@Test
	public void createGroupTest() throws PeerGroupException, RuntimeException, IOException{
		node.start(9800);
		PeerGroup defaultPg = node.getDefaultPeerGroup();
		String groupName = TestInputGenerator.getRandomUser(10);
		PeerGroup pg = node.createGroup(groupName);
		node.createGroup(groupName);
		assertFalse(defaultPg.getPeerGroupName().equals(pg.getPeerGroupName()));
		assertTrue(defaultPg.getPeerID().equals(pg.getPeerID()));
		assertTrue(node.getPeerId().equals(pg.getPeerID().toString()));
	}
	
	@Test
	public void stopExceptionTest(){
		JxtaNode nodetmp = new JxtaNode();
		exception.expect(RuntimeException.class);
		exception.expectMessage("Serveur was not started !");	
		nodetmp.stop();
	}
	
	@Test
	public void stopTest() throws PeerGroupException, RuntimeException, IOException{
		JxtaNode node3 = new JxtaNode();
		try {
			node3.initialize(cache3, "testNode3", true);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		node3.start(9800);
		assertTrue(node3.isStarted());
		node3.stop();
		assertFalse(node3.isStarted());
	}
}

