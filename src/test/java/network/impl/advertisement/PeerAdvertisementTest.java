package network.impl.advertisement;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.jdom2.Document;
import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;

import network.api.annotation.AdvertisementAttribute;
import network.factories.AdvertisementFactory;
import util.TestInputGenerator;

/**
 * PeerAdvertisement unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class PeerAdvertisementTest {
	PeerAdvertisement peerAdv;
	String name;
	private Element elem;
	private Document doc;
	private String sourceURI;

	@Before
	public void instantiate(){
		peerAdv = (PeerAdvertisement) AdvertisementFactory.createPeerAdvertisement();
		name = "peer";
		elem = new Element(TestInputGenerator.getRandomAlphaWord());
		doc = new Document (elem);
		sourceURI = TestInputGenerator.getRandomAlphaWord();
	}

	@Test
	public void getterTest() {
		assertTrue(name.equals(peerAdv.getName()));  
		assertTrue(peerAdv.getAdvertisementType() == null);

		peerAdv.initialize(doc);
		assertTrue(peerAdv.getDocument().getRootElement().getName().equals(name));

		peerAdv.setSourceURI(sourceURI);
		assertTrue(peerAdv.getSourceURI().equals(sourceURI));

	}
	
	@Test
	public void fieldsAnnotationsTest(){
		//publicKey
		try{
			Field pkeyField = peerAdv.getClass().getDeclaredField("publicKey");
			assertTrue(pkeyField.getAnnotation(AdvertisementAttribute.class).enabled());			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
