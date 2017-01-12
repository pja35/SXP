package network.impl.advertisement;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import network.impl.advertisement.PeerAdvertisement;

import network.api.annotation.AdvertisementAttribute;

import org.jdom2.Document;
import org.jdom2.Element;

import network.factories.AdvertisementFactory;
import util.TestInputGenerator;
import java.lang.reflect.Field;

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
