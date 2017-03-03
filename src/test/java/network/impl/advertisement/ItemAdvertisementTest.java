package network.impl.advertisement;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import network.impl.advertisement.ItemAdvertisement;
import network.impl.jxta.JxtaItemService;
import network.impl.jxta.JxtaPeer;
import util.TestInputGenerator;
import util.TestUtils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.*;

import network.api.annotation.AdvertisementAttribute;
import network.api.annotation.ServiceName;
import network.factories.AdvertisementFactory;
import network.factories.PeerFactory;



/**
 * ItemAdvertisement unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class ItemAdvertisementTest{
	private final static Logger log = LogManager.getLogger(ItemAdvertisementTest.class);
	
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
	
	ItemAdvertisement itemAdv;
	String name;
	String title;
	private Element elem;
	private Document doc;
	private String sourceURI;
	
	@Before
	public void instantiate(){
		itemAdv = (ItemAdvertisement) AdvertisementFactory.createItemAdvertisement();
		title = TestInputGenerator.getRandomIpsumString(100);
		itemAdv.setTitle(title);
		name = "item";
		elem = new Element(TestInputGenerator.getRandomAlphaWord());
		doc = new Document (elem);
		sourceURI = TestInputGenerator.getRandomAlphaWord();
	}
	
	@Test
	public void getterTest() {
		assertTrue(name.equals(itemAdv.getName()));
		assertTrue(title.equals(itemAdv.getTitle()));
		assertTrue(itemAdv.getAdvertisementType() == null);

		itemAdv.initialize(doc);
		assertTrue(itemAdv.getDocument().getRootElement().getName().equals(name));

		itemAdv.setSourceURI(sourceURI);
		assertTrue(itemAdv.getSourceURI().equals(sourceURI));

	}
	
	@Test
	public void fieldsAnnotationsTest(){
		//title
		try{
			Field titleField = itemAdv.getClass().getDeclaredField("title");
			assertTrue(titleField.getAnnotation(AdvertisementAttribute.class).indexed());			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void classAnnotationsTest(){
		assertTrue(itemAdv.getClass().getAnnotation(ServiceName.class).name().equals("items"));
	}
	
	@Test
	public void publishTest(){
		//Test no exception has been raised.
		try{
			itemAdv.publish(jxtaPeer);
		}catch(Exception e){
			log.debug(e.getMessage());
			fail();
		}	
	}
}
