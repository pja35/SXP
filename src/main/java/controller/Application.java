package controller;

import java.util.Properties;

import controller.tools.LoggerUtilities;
import model.syncManager.UserSyncManagerImpl;
import network.api.EstablisherService;
import network.api.Messages;
import network.api.Peer;
import network.api.ServiceListener;
import network.api.advertisement.EstablisherAdvertisementInterface;
import network.factories.AdvertisementFactory;
import network.factories.PeerFactory;
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
	public final static int jxtaPort = 9801;
	public final static int restPort = 8081;
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
		Application.getInstance().runForTests(8081);
		
		final Peer peer=Application.getInstance().getPeer();
		
		// Sending an advertisement (trick to get the other peer URI)
		EstablisherAdvertisementInterface cadv = AdvertisementFactory.createEstablisherAdvertisement();
		cadv.setTitle("Un Contrat");
		cadv.publish(peer);
		
		// Listener on establisher events
		final EstablisherService establisher =(EstablisherService) peer.getService(EstablisherService.NAME);
		establisher.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				Integer m1 = new Integer(messages.getMessage("contract"));
				String msg = String.valueOf(m1 + 1);
				if (m1<6) {
					establisher.sendContract("Contrat "+msg, "test", "test2", msg, messages.getMessage("source"));
				}
				try{
					Thread.sleep(1000);
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}, "test2");
		
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
