package network.impl.jxta;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;

import controller.tools.LoggerUtilities;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.endpoint.EndpointAddress;
import net.jxta.endpoint.EndpointService;
import net.jxta.endpoint.Message;
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.platform.NetworkManager;
import network.api.Peer;
import network.api.service.InvalidServiceException;
import network.api.service.Service;
import network.utils.IpChecker;

public class JxtaPeer implements Peer{

	private JxtaNode node;
	private HashMap<String, Service> services;
	
	/**
	 * Create a new Peer (Jxta implementation)
	 */
	public JxtaPeer() {
		node = new JxtaNode();
		services = new HashMap<>();
	}
	public JxtaPeer(int port) {
		node = new JxtaNode(port);
		services = new HashMap<>();
	}
	
	@Override
	public void start(String cache, int port, String ...bootstrap) throws IOException, PeerGroupException, RuntimeException {
		node.initialize(cache, "sxp peer", true);
		this.bootstrap(bootstrap);
		node.start(port);
	}

	@Override
	public void stop() {
		node.stop();
	}

	@Override
	public String getIp() {
		try {
			return IpChecker.getIp();
		} catch (Exception e) {
			LoggerUtilities.logStackTrace(e);
		}
		return null;
	}

	@Override
	public Collection<Service> getServices() {
		return services.values();
	}

	@Override
	public Service getService(String name) {
		return services.get(name);
	}

	@Override
	public void addService(Service service) throws InvalidServiceException {
		if (service.getName() == null || service.getName().isEmpty()){
			throw new InvalidServiceException("Service name is empty");
		}
		JxtaService s = (JxtaService) service;
		services.put(service.getName(), service);
		s.setPeerGroup(node.createGroup(service.getName()));
	}
	
//	public static void main(String[] args) {
//		JxtaPeer peer = new JxtaPeer();
//		try {
//			peer.start(".test", 9800);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			LoggerUtilities.logStackTrace(e);
//		}
//	}

	@Override
	public String getUri() {
		return node.getPeerId();
	}

	@Override
	public void bootstrap(String... ips) {
		NetworkManager networkManager = node.getNetworkManager();
		for(String ip : ips) {
			URI theSeed = URI.create(ip);
			
			try {
				System.out.println("server added :" + theSeed);
				networkManager.getConfigurator().addSeedRendezvous(theSeed);
			} catch (IOException e) {
				LoggerUtilities.logStackTrace(e);
			}
		}
		
		
	}

}
