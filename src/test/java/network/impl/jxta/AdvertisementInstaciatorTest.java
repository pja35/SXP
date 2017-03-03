package network.impl.jxta;

import static org.junit.Assert.assertTrue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import net.jxta.document.Advertisement;
import network.factories.AdvertisementFactory;


/**
 * AdvertisementInstaciator unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class AdvertisementInstaciatorTest {
	private final static Logger log = LogManager.getLogger(AdvertisementInstaciatorTest.class);
	
	private AdvertisementBridge advBridge;
	private AdvertisementInstanciator instantiator;
	
	
	
	public AdvertisementInstaciatorTest() {
		this.advBridge = new AdvertisementBridge(AdvertisementFactory.createItemAdvertisement());
	}

	@Test
	public void constructionTest(){
		instantiator = new AdvertisementInstanciator(advBridge);
		log.debug(instantiator.getAdvertisementType());
		assertTrue(instantiator.getAdvertisementType().equals("jxta:network.impl.jxta.AdvertisementBridge"));
	}
	
	@Test
	public void instantiationTest() {
		instantiator = new AdvertisementInstanciator(advBridge);
		Advertisement adv = instantiator.newInstance();
		log.debug(adv.getAdvType());
		assertTrue(adv.getAdvType().equals("jxta:network.impl.jxta.AdvertisementBridge"));
		assertTrue(instantiator.newInstance(null) == null);
	}

}
