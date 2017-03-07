package network.impl.jxta;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import network.api.advertisement.Advertisement;
import network.factories.AdvertisementFactory;
import network.factories.PeerFactory;
import util.TestInputGenerator;
import util.TestUtils;

/**
 * JxtaAdvertisement unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class JxtaAdvertisementTest {
	private final static Logger log = LogManager.getLogger(JxtaAdvertisementTest.class);
	
	static private String cache = TestInputGenerator.getRandomCacheName();
	static private JxtaPeer jxtaPeer;

	@BeforeClass
	public static void setUpClass() {
		log.debug("**************** Starting test");
		TestUtils.removeRecursively(new File(cache));
		jxtaPeer = PeerFactory.createJxtaPeer();
		try {
			jxtaPeer.start(cache, 9800);
			jxtaPeer.addService(new JxtaItemService());
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
	}

	@AfterClass
	public static void tearDownClass() {
		jxtaPeer.stop();
		TestUtils.removeRecursively(new File(cache));
	}
	
	private Advertisement adv;
	private JxtaAdvertisement jxtaAdv;
	private Element elem;
	private Document doc;
	private String sourceURI;
	
	public JxtaAdvertisementTest() {
		this.adv = AdvertisementFactory.createItemAdvertisement();
		this.jxtaAdv = new JxtaAdvertisement(adv);
		elem = new Element(TestInputGenerator.getRandomAlphaWord());
		doc = new Document (elem);
		sourceURI = TestInputGenerator.getRandomAlphaWord();
	}


	@Test
	public void bridgeTest(){
		AdvertisementBridge JxtaAdvBridge = jxtaAdv.getJxtaAdvertisementBridge();
		assertTrue(Arrays.equals(jxtaAdv.getIndexFields(),JxtaAdvBridge.getIndexFields()));
	}

	@Test
	public void getterTest() {
		assertTrue(jxtaAdv.getName().equals(adv.getName()));
		assertTrue(jxtaAdv.getAdvertisementType().equals("jxta:" + adv.getName()));
		assertTrue(Arrays.equals(jxtaAdv.getIndexFields(),adv.getIndexFields()));
			
		jxtaAdv.initialize(doc);
		assertTrue(jxtaAdv.getDocument().getRootElement().getName().equals(adv.getName()));
		
		jxtaAdv.setSourceURI(sourceURI);
		assertTrue(jxtaAdv.getSourceURI().equals(sourceURI));		
	}
	
	@Test
	public void publishTest(){
		//Test no exception has been raised.
		try{
			jxtaAdv.publish(jxtaPeer);
		}catch(Exception e){
			log.debug(e.getMessage());
			fail();
		}	
	}
}

