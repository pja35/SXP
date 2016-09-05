package network.factories;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jxta.document.AdvertisementFactory;
import network.api.Peer;
import network.api.service.Service;
import network.impl.jxta.AdvertisementBridge;
import network.impl.jxta.AdvertisementInstaciator;
import network.impl.jxta.JxtaItemService;
import network.impl.jxta.JxtaItemsSenderService;
import network.impl.jxta.JxtaPeer;

/**
 * {@link Peer} factory
 * @author Julien Prudhomme
 *
 */
public class PeerFactory {
	
	/**
	 * create the default implementation of {@link Peer}
	 * @return a {@link Peer}
	 */
	public static Peer createDefaultPeer() {
		return createJxtaPeer();
	}
	
	/**
	 * Create a the default implementation of {@link Peer} and start it
	 * @return an initialized and started {@link Peer}
	 */
	public static Peer createDefaultAndStartPeer() {
		Peer p = createAndStartPeer("jxta", ".peercache", 9578);
		Service itemService = new JxtaItemService();
		itemService.initAndStart(p);
		return p;
	}
	
	public static Peer createDefaultAndStartPeerForTest() {
		Random r = new Random();
		String cache = ".peer" + r.nextInt(10000);
		//int port = 9800 + r.nextInt(100);
		int port = 9800;
		System.out.println("jxta will run on port " + port);
		Peer p = createAndStartPeer("jxta", cache, port);
		
		Service itemService = new JxtaItemService();
		itemService.initAndStart(p);
		
		Service itemsSender = new JxtaItemsSenderService();
		itemsSender.initAndStart(p);
		return p;
	}
	
	/**
	 * Create a Jxta implementation of {@link Peer}
	 * @return a {@link JxtaPeer}
	 */
	public static JxtaPeer createJxtaPeer() {
		Logger.getLogger("net.jxta").setLevel(Level.SEVERE);
		AdvertisementBridge i = new AdvertisementBridge();
		AdvertisementFactory.registerAdvertisementInstance(i.getAdvType(), new AdvertisementInstaciator(i));
		return new JxtaPeer();
	}
	
	/**
	 * Create, initialize, and start a {@link JxtaPeer}
	 * @return an initialized and started {@link Peer}
	 */
	public static Peer createAndStartPeer(String impl, String tmpFolder, int port) {
		Peer peer;
		switch(impl) {
		case "jxta": peer = createJxtaPeer(); break;
		default: throw new RuntimeException(impl + "doesn't exist");
		}
		try {
			peer.start(tmpFolder, port, "tcp://109.15.222.135:9800");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return peer;
	}
}
