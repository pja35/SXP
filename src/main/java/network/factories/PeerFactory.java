package network.factories;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import controller.Application;
import controller.tools.LoggerUtilities;
import net.jxta.document.AdvertisementFactory;
import network.api.Peer;
import network.api.service.InvalidServiceException;
import network.api.service.Service;
import network.impl.jxta.AdvertisementBridge;
import network.impl.jxta.AdvertisementInstanciator;
import network.impl.jxta.JxtaEstablisherService;
import network.impl.jxta.JxtaItemService;
import network.impl.jxta.JxtaItemsSenderService;
import network.impl.jxta.JxtaMessageSenderService;
import network.impl.jxta.JxtaMessageService;
import network.impl.jxta.JxtaPeer;
import network.impl.jxta.JxtaUserService;
import network.impl.jxta.JxtaUsersSenderService;

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

		try {
			System.out.println("\n START Services \n");
			
			Service userService = new JxtaUserService();
			userService.initAndStart(p);
			
			Service usersSender = new JxtaUsersSenderService();
			usersSender.initAndStart(p);
			
			Service itemService = new JxtaItemService();
			itemService.initAndStart(p);
			
			Service itemsSender = new JxtaItemsSenderService();
			itemsSender.initAndStart(p);
			
			Service messageService = new JxtaMessageService();
			messageService.initAndStart(p);
			
			Service messagesSender = new JxtaMessageSenderService();
			messagesSender.initAndStart(p);
			
			Service establisherService = new JxtaEstablisherService();
			establisherService.initAndStart(p);
			
		} catch (InvalidServiceException e) {
			throw new RuntimeException(e);
		}		
//		Service itemService = new JxtaItemService();
//		Service establisherService = new JxtaEstablisherService();
//		
//		try {
//			itemService.initAndStart(p);
//			establisherService.initAndStart(p);
//		} catch (InvalidServiceException e) {
//			// TODO manage the exception
//			LoggerUtilities.logStackTrace(e);
//		}
		return p;
	}

	
	public static Peer createDefaultAndStartPeerForTest() {
		return createDefaultAndStartPeerForTest(Application.jxtaPort, Application.rdvPeerIds);
	}
	
	public static Peer createDefaultAndStartPeerForTest(int port, String[] rdvPeerIds) {
		Random r = new Random();
		String cache = ".peercache" + r.nextInt(10000);
		//int port = 9800 + r.nextInt(100);
		System.out.println("jxta will run on port " + port);
		Peer p = createAndStartPeer("jxta", cache, port, rdvPeerIds);
		
		try {
			System.out.println("\n START Services \n");
			
			Service userService = new JxtaUserService();
			userService.initAndStart(p);
			
			Service usersSender = new JxtaUsersSenderService();
			usersSender.initAndStart(p);
			
			Service itemService = new JxtaItemService();
			itemService.initAndStart(p);
			
			Service itemsSender = new JxtaItemsSenderService();
			itemsSender.initAndStart(p);
			
			Service messageService = new JxtaMessageService();
			messageService.initAndStart(p);
			
			Service messagesSender = new JxtaMessageSenderService();
			messagesSender.initAndStart(p);
			
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
	public static JxtaPeer createJxtaPeer(){
		return createJxtaPeer(Application.jxtaPort);
	}
	public static JxtaPeer createJxtaPeer(int port) {
		Logger.getLogger("net.jxta").setLevel(Level.SEVERE);
		AdvertisementBridge i = new AdvertisementBridge();
		AdvertisementFactory.registerAdvertisementInstance(i.getAdvType(), new AdvertisementInstanciator(i));
		return new JxtaPeer(port);
	}
	

	public static Peer createAndStartPeer(String impl, String tmpFolder, int port){
		return createAndStartPeer(impl, tmpFolder, port, Application.rdvPeerIds);
	}
	/**
	 * Create, initialize, and start a {@link JxtaPeer}
	 * @return an initialized and started {@link Peer}
	 */
	public static Peer createAndStartPeer(String impl, String tmpFolder, int port, String[] rdvPeerIds){
		Peer peer;
		switch(impl) {
		case "jxta": peer = createJxtaPeer(port); break;
		default: throw new RuntimeException(impl + "doesn't exist");
		}
		try {

			peer.start(tmpFolder, port, rdvPeerIds);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return peer;
	}
}
