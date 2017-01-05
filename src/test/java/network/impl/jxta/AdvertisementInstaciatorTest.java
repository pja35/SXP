package network.impl.jxta;

import org.junit.Test;

import net.jxta.document.Advertisement;

import static org.junit.Assert.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import network.factories.AdvertisementFactory;
import network.impl.jxta.AdvertisementInstaciator;
import network.impl.messages.RequestItemMessageTest;


/**
 * AdvertisementInstaciator unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class AdvertisementInstaciatorTest {
	private final static Logger log = LogManager.getLogger(AdvertisementInstaciatorTest.class);
	
	private AdvertisementBridge advBridge;
	private AdvertisementInstaciator instantiator;
	
	
	
	public AdvertisementInstaciatorTest() {
		this.advBridge = new AdvertisementBridge(AdvertisementFactory.createItemAdvertisement());
	}

	@Test
	public void constructionTest(){
		instantiator = new AdvertisementInstaciator(advBridge);
		log.debug(instantiator.getAdvertisementType());
		assertTrue(instantiator.getAdvertisementType().equals("jxta:network.impl.jxta.AdvertisementBridge"));
	}
	
	@Test
	public void instantiationTest() {
		instantiator = new AdvertisementInstaciator(advBridge);
		Advertisement adv = instantiator.newInstance();
		log.debug(adv.getAdvType());
		assertTrue(adv.getAdvType().equals("jxta:network.impl.jxta.AdvertisementBridge"));
		assertTrue(instantiator.newInstance(null) == null);
	}

}
