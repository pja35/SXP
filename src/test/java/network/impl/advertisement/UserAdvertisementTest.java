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
 * UserAdvertisement unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class UserAdvertisementTest {
	UserAdvertisement user;
	String name;
	private Element elem;
	private Document doc;
	private String sourceURI;

	@Before
	public void instantiate(){
		user = (UserAdvertisement) AdvertisementFactory.createUserAdvertisement();
		name = "user";
		elem = new Element(TestInputGenerator.getRandomAlphaWord());
		doc = new Document (elem);
		sourceURI = TestInputGenerator.getRandomAlphaWord();
	}

	@Test
	public void getterTest() {
		assertTrue(name.equals(user.getName()));  
		assertTrue(user.getAdvertisementType() == null);

		user.initialize(doc);
		assertTrue(user.getDocument().getRootElement().getName().equals(name));

		user.setSourceURI(sourceURI);
		assertTrue(user.getSourceURI().equals(sourceURI));

	}

	@Test
	public void fieldsAnnotationsTest(){
		//nickName
		try{
			Field nickField = user.getClass().getDeclaredField("nick");
			assertTrue(nickField.getAnnotation(AdvertisementAttribute.class).indexed());
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		//publicKey
		try{
			Field pkeyField = user.getClass().getDeclaredField("pbkey");
			assertTrue(pkeyField.getAnnotation(AdvertisementAttribute.class).indexed());			
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}


