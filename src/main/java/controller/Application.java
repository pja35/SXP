package controller;

import java.util.Enumeration;
import java.util.Properties;

import controller.tools.LoggerUtilities;
import model.syncManager.UserSyncManagerImpl;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import network.api.EstablisherService;
import network.api.Peer;
import network.api.advertisement.EstablisherAdvertisementInterface;
import network.factories.AdvertisementFactory;
import network.factories.PeerFactory;
import network.impl.advertisement.EstablisherAdvertisement;
import network.impl.jxta.AdvertisementBridge;
import rest.api.Authentifier;
import rest.factories.AuthentifierFactory;
import rest.factories.RestServerFactory;

/**
 * Main class
 * {@link Application} is a singleton
 * @author Julien Prudhomme
 *
 */
public class Application {
	public final static int jxtaPort = 9800;
	public final static int restPort = 8080;
	public final static String[] rdvPeerIds = {"tcp://176.132.64.68:9800", "tcp://localhost:9800"};
	
	private static Application instance = null;
	private static UserSyncManagerImpl umg;
	private Peer peer;
	private Authentifier auth;

	
	
	public Application() {
		if(instance != null) {
			throw new RuntimeException("Application can be instanciate only once !");
		}
		instance = this;
	}

	public static Application getInstance()	{
		return instance;
	}

	public void run() {
		setPeer(PeerFactory.createDefaultAndStartPeer());
		setAuth(AuthentifierFactory.createDefaultAuthentifier());
		RestServerFactory.createAndStartDefaultRestServer(8080); //start the rest api
	}

	public void runForTests(int restPort) {
		Properties p = System.getProperties();
		p.put("derby.system.home", "./.db-" + restPort + "/");
		umg = new UserSyncManagerImpl(); //just init the db
		umg.close();
		umg = null;
		try {
			setPeer(PeerFactory.createDefaultAndStartPeerForTest());
			setAuth(AuthentifierFactory.createDefaultAuthentifier());
			RestServerFactory.createAndStartDefaultRestServer(restPort);
		} catch (Exception e) {
			LoggerUtilities.logStackTrace(e);
		}		
	}

	public static void main(String[] args) {
		new Application();
		Application.getInstance().runForTests(8080);

		final Peer peer=Application.getInstance().getPeer();
		
		// Sending an advertisement (trick to get the other peer URI)
		EstablisherAdvertisementInterface cadv = AdvertisementFactory.createEstablisherAdvertisement();
		cadv.setTitle("Contract");
		cadv.setContract("1");
		cadv.publish(peer);
		
		final EstablisherService establisher =(EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);
		
		establisher.addAdvertisementListener(new DiscoveryListener(){
			@Override
			public void discoveryEvent(DiscoveryEvent event){
				Enumeration<net.jxta.document.Advertisement> adverts = event.getResponse().getAdvertisements();
				while (adverts.hasMoreElements()){
					AdvertisementBridge adv = (AdvertisementBridge) adverts.nextElement();
					if (adv.getAdvertisement().getClass().equals(EstablisherAdvertisement.class)){
						EstablisherAdvertisement c = (EstablisherAdvertisement) adv.getAdvertisement();
						System.out.println("\n" + adv.getAdvertisement().getClass().equals(EstablisherAdvertisement.class));
						System.out.println(c.getTitle());
						System.out.println(c.getContract() + "\n");
						
						Integer i = new Integer(c.getContract());
						

						try{
							Thread.sleep(2000);
						}catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						if (i<3){
							final Peer peer=Application.getInstance().getPeer();
							
							// Sending an advertisement (trick to get the other peer URI)
							EstablisherAdvertisementInterface cadv = AdvertisementFactory.createEstablisherAdvertisement();
							cadv.setTitle("Contract");
							cadv.setContract(String.valueOf(i.intValue() + 1));
							
							cadv.publish(peer);
						}
					}
				}
			}
		});
	}
	
	public void stop(){
		peer.stop();
		instance = null;
	}
	
	public Peer getPeer() {
		return peer;
	}

	public void setPeer(Peer peer) {
		this.peer = peer;
	}

	public Authentifier getAuth() {
		return auth;
	}

	public void setAuth(Authentifier auth) {
		this.auth = auth;
	}
}
