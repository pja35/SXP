package network.impl.jxta;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;

import net.jxta.platform.NetworkManager;
import network.api.Peer;
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
	
	@Override
	public void start(String cache, int port, String ...bootstrap) throws IOException {
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
			e.printStackTrace();
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
	public void addService(Service service) {
		JxtaService s = (JxtaService) service;
		services.put(service.getName(), service);
		s.setPeerGroup(node.createGroup(service.getName()));
	}
	
	public static void main(String[] args) {
		JxtaPeer peer = new JxtaPeer();
		try {
			peer.start(".test", 9800);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
				e.printStackTrace();
			}
		}
		
	}

}
