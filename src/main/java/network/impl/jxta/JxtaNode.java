package network.impl.jxta;

import java.io.File;
import java.io.IOException;

import net.jxta.exception.PeerGroupException;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.NetworkConfigurator;
import net.jxta.platform.NetworkManager;
import net.jxta.protocol.ModuleImplAdvertisement;
import network.api.Node;
import network.utils.IpChecker;

/**
 * Default JXTA Node implementation. Represent the server node.
 * When successfully started, yield an Advertisement that represent this node.
 * @see Node
 * @author Julien Prudhomme
 */
public class JxtaNode implements Node{

	private NetworkManager networkManager = null;
	private boolean initialized = false;
	private PeerGroup defaultPeerGroup = null;
	
	@Override
	public void initialize(String cacheFolder, String name, boolean persistant) throws IOException {
		File configFile = new File("." + System.getProperty("file.separator") + cacheFolder); /* file used by the networkManager */
		networkManager = initializeNetworkManager(configFile, name, persistant);
		//no errors
		initialized = true;
	}

	public NetworkManager getNetworkManager() {
		return networkManager;
	}
	
	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public void start(int port) throws RuntimeException {
		if(!initialized) {
			throw new RuntimeException("Node must be initalized before start call");
		}
		try {
			NetworkConfigurator configurator = networkManager.getConfigurator();
			configurator.setTcpPort(port);
			configurator.setHttpPort(port + 1);
			PeerGroup pg = networkManager.startNetwork();
			pg.startApp(new String[0]);
			//Switch to rendez vous mode if possible, check every 60 secs
			pg.getRendezVousService().setAutoStart(true,60*1000);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error on config file");
			System.exit(-1);
		} catch (PeerGroupException e) {
			e.printStackTrace();
			System.err.println("error while creating main peer group");
			System.exit(-1);
			//can't continue 
		}
		
		createDefaultGroup();
		
	}
	
	@Override
	public boolean isStarted() {
		return isInitialized() && networkManager.isStarted();
	}
	
	/**
	 * Initialize the network manager
	 * @param configFile
	 * @param peerName
	 * @param persistant
	 * @return
	 * @throws IOException
	 */
	private NetworkManager initializeNetworkManager(File configFile, String peerName, boolean persistant) throws IOException {
		NetworkManager manager = null;
		NetworkConfigurator configurator = null;
		manager = new NetworkManager(NetworkManager.ConfigMode.EDGE, peerName, configFile.toURI()); /* Setting network */
		configurator = manager.getConfigurator(); /* Getting configurator for future tweaks */
        configurator.setTcpEnabled(true);
        configurator.setHttpEnabled(true);
        configurator.setTcpIncoming(true);
        configurator.setHttpIncoming(true);
        configurator.setHttpOutgoing(true);
        configurator.setTcpOutgoing(true);
        configurator.setUseMulticast(true);
		configurator.setTcpInterfaceAddress("0.0.0.0");
		configurator.setUseMulticast(true);
		try {
			configurator.setTcpPublicAddress(IpChecker.getIp(), false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		configurator.setHttpInterfaceAddress("0.0.0.0");
		try {
			configurator.setHttpPublicAddress(IpChecker.getIp(), false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        configurator.setTcpEndPort(-1);
        configurator.setTcpStartPort(-1);
        configurator.setName("SXPeerGroup");
        configurator.setDescription("SXP default peer group");
        configurator.setPrincipal("SXP peer group");
        manager.setConfigPersistent(persistant);
		return manager;
	}

	@Override
	public void stop() {
		if(networkManager == null) {
			throw new RuntimeException("Serveur was not started !");
		}
		networkManager.stopNetwork();
	}
	
	protected PeerGroup getDefaultPeerGroup() {
		return this.defaultPeerGroup;
	}
	
	private PeerGroupID generatePeerGroupID(PeerGroupID parent, String peerGroupName) {
		return IDFactory.newPeerGroupID(PeerGroupID.defaultNetPeerGroupID, peerGroupName.getBytes());
	}
	
	private void createDefaultGroup() {
		try {
			PeerGroup netpeerGroup = networkManager.getNetPeerGroup();
			
			ModuleImplAdvertisement madv = null;
			try {
				madv = netpeerGroup.getAllPurposePeerGroupImplAdvertisement();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			defaultPeerGroup = netpeerGroup.newGroup(this.generatePeerGroupID(netpeerGroup.getPeerGroupID(), "SXP group"),
					madv, "SXP group", "SXP group", true);
			defaultPeerGroup.startApp(new String[0]);
			defaultPeerGroup.getRendezVousService().setAutoStart(true, 60*1000);
		} catch (PeerGroupException e) {
			System.err.println("impossible to create default group");
			e.printStackTrace();
			System.exit(-1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Group created !");
	}
	
	protected PeerGroup createGroup(final String name) {
		ModuleImplAdvertisement mAdv = null;
		PeerGroup temp = null;
		System.out.println("creating new group ..");
		try {
			mAdv = defaultPeerGroup.getAllPurposePeerGroupImplAdvertisement();
			temp = defaultPeerGroup.newGroup(generatePeerGroupID(defaultPeerGroup.getPeerGroupID(), name), mAdv, name, name, true); /* creating & publishing the group */
			getDefaultPeerGroup().getDiscoveryService().remotePublish(temp.getPeerGroupAdvertisement());
			temp.startApp(new String[0]);
			temp.getRendezVousService().setAutoStart(true, 60);
		} catch (Exception e) {
			e.printStackTrace();
		} /* Getting the advertisement of implemented modules */
		return temp;
	}
	
	public String getPeerId() {
		return this.defaultPeerGroup.getPeerID().toURI().toString();
	}
}
