package controller;

import java.util.Properties;

import controller.tools.LoggerUtilities;
import model.syncManager.UserSyncManagerImpl;
import network.api.Peer;
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
		
		/*
		 Properties p = System.getProperties();
		
		Enumeration e = p.propertyNames();

	    while (e.hasMoreElements()) {
	      String key = (String) e.nextElement();
	      System.out.println(key + " -- " + p.getProperty(key));
	    }
		
		System.out.println("derby.system.home : " + p.getProperty("derby.system.home"));
		 */
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
