package network.factories;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import controller.tools.LoggerUtilities;
import net.jxta.document.AdvertisementFactory;
import net.jxta.exception.PeerGroupException;
import network.api.Peer;
import network.api.service.InvalidServiceException;
import network.api.service.Service;
import network.impl.jxta.AdvertisementBridge;
import network.impl.jxta.AdvertisementInstanciator;
import network.impl.jxta.JxtaItemService;
import network.impl.jxta.JxtaItemsSenderService;
import network.impl.jxta.JxtaEstablisherService;
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
		Service establisherService = new JxtaEstablisherService();
		
		try {
			itemService.initAndStart(p);
			establisherService.initAndStart(p);
		} catch (InvalidServiceException e) {
			// TODO manage the exception
			LoggerUtilities.logStackTrace(e);
		}
		return p;
	}
	
	public static Peer createDefaultAndStartPeerForTest() {
		Random r = new Random();
		String cache = ".peer" + r.nextInt(10000);
		//int port = 9800 + r.nextInt(100);
		int port = 9800;
		System.out.println("jxta will run on port " + port);
		Peer p = createAndStartPeer("jxta", cache, port);
		
		try {
			Service itemService = new JxtaItemService();
			itemService.initAndStart(p);
			Service itemsSender = new JxtaItemsSenderService();
			itemsSender.initAndStart(p);
			Service establisherService = new JxtaEstablisherService();
			establisherService.initAndStart(p);
		} catch (InvalidServiceException e) {
			throw new RuntimeException(e);
		}		
		return p;
	}
	
	/**
	 * Create a Jxta implementation of {@link Peer}
	 * @return a {@link JxtaPeer}
	 */
	public static JxtaPeer createJxtaPeer() {
		Logger.getLogger("net.jxta").setLevel(Level.SEVERE);
		AdvertisementBridge i = new AdvertisementBridge();
		AdvertisementFactory.registerAdvertisementInstance(i.getAdvType(), new AdvertisementInstanciator(i));
		return new JxtaPeer();
	}
	
	/**
	 * Create, initialize, and start a {@link JxtaPeer}
	 * @return an initialized and started {@link Peer}
	 */
	public static Peer createAndStartPeer(String impl, String tmpFolder, int port){
		Peer peer;
		switch(impl) {
		case "jxta": peer = createJxtaPeer(); break;
		default: throw new RuntimeException(impl + "doesn't exist");
		}
		try {
			peer.start(tmpFolder, port, "tcp://109.15.222.135:9800");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return peer;
	}
}
