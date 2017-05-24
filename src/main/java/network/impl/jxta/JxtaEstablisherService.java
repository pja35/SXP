package network.impl.jxta;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import crypt.api.key.AsymKey;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.pipe.PipeMsgEvent;
import network.api.EstablisherService;
import network.api.EstablisherServiceListener;
import network.api.Messages;
import network.api.Peer;
import network.api.SearchListener;
import network.api.ServiceListener;
import network.api.advertisement.EstablisherAdvertisementInterface;
import network.factories.AdvertisementFactory;
import network.impl.advertisement.EstablisherAdvertisement;
import network.impl.messages.EstablisherMessage;

/**
 * @author Nathanaël EON
 *
 */
public class JxtaEstablisherService extends JxtaService implements EstablisherService{
	public static final String NAME = "establisher";

	private ConcurrentHashMap<String, DiscoveryListener> advertisementListeners;
	// Hashmap of localListeners
	private ConcurrentHashMap<String, ListenerWithParam> establisherServiceListeners;
	
	// Local listener (to be able to notify local users connected if this peer send an advertisement)
	private class ListenerWithParam{
		public final String param;
		public final EstablisherServiceListener listener;
		
		public ListenerWithParam(String p, EstablisherServiceListener l){
			this.param = p;
			this.listener = l;
		}
	}
	
	
	
	public JxtaEstablisherService (){ 
		this.name = NAME;
		advertisementListeners = new ConcurrentHashMap<String, DiscoveryListener>();
		establisherServiceListeners = new ConcurrentHashMap<String, ListenerWithParam>();
	}
	


	/*
	 *  Encapsulate contract sending, through Advertisement or Messages
	 *  If uris == null & peer != null => Use Advertisements
	 *  Else if uris != null => Use messages
	 *  
	 *  Careful, we send on the listener : title + receiverPublicKey
	 *  If you use this system, you must set the listener according to this
	 */
	@Override
	public <Key extends AsymKey<?>> void sendContract(String title, String data, String senderK, Peer peer, HashMap<Key,String> uris){
		// Using an Advertisement
		if (uris == null && peer != null){
			this.sendContract(title, data, senderK, peer);
		}
		// Using a Message
		else if(uris != null){
			for (Key u : uris.keySet()){
				this.sendContract(title,
						title + u.getPublicKey().toString(), 
						senderK,
						data,
						uris.get(u));				
			}
		}
	}
	
	// Send a message
	@Override
	public EstablisherMessage sendContract(String title, String who, String sourceId, String contract, String... peerURIs) 
	{
		EstablisherMessage m = new EstablisherMessage();
		m.setTitle(title);
		m.setWho(who);
		m.setSourceId(sourceId);
		m.setSource(this.peerUri);
		m.setContract(contract);
		this.sendMessages(m, peerURIs);
		return m;
	}
	
	// Send an advertisement
	@Override
	public void sendContract(String title, String data, String sourceKey, Peer peer){
		EstablisherAdvertisementInterface cadv = AdvertisementFactory.createEstablisherAdvertisement();
		cadv.setTitle(title);
		cadv.setContract(data);
		cadv.setKey(sourceKey);
		cadv.publish(peer);
		// Notify local listeners of an event
		for (ListenerWithParam l :  establisherServiceListeners.values()){
			if (l.param == null || l.param.equals(title))
				l.listener.notify(title, data, sourceKey);
		}
	}
	
	
	
	/**
	 * Method called when a message is caught in the pipe
	 */
	@Override
	public void pipeMsgEvent(PipeMsgEvent event) {
		super.pipeMsgEvent(event);
	}
	
	
	
	/*
	 * Encapsulate listener adding
	 */
	public void setListener(final String field, final String value, String listenerId, final EstablisherServiceListener l, boolean useMessage){
		if (useMessage){
			// Set a Message listener
			this.addListener(new ServiceListener() {
				@Override
				public void notify(Messages messages) {
					if (messages.getMessage(field).equals(value)){
						l.notify(messages.getMessage("title"), messages.getMessage("contract"), messages.getMessage("sourceId"));
					}
				}
			}, listenerId);
		}else{
			// Set an Advertisement
			this.listens(field, value, listenerId, l);
		}
	}
	
	// Advertisement listener
	@Override
	public void listens(final String field, final String value, String listenerId, final EstablisherServiceListener l){
		
		// Create the synchrone listener
		DiscoveryListener dl = new DiscoveryListener(){
			@Override
			public void discoveryEvent(DiscoveryEvent event){
				Enumeration<net.jxta.document.Advertisement> adverts = event.getResponse().getAdvertisements();
				while (adverts.hasMoreElements()){
					AdvertisementBridge adv = (AdvertisementBridge) adverts.nextElement();
					if (adv.getAdvertisement().getClass().equals(EstablisherAdvertisement.class)){
						EstablisherAdvertisementInterface c = (EstablisherAdvertisementInterface) adv.getAdvertisement();
						// If the field on which to listen is the title, then we check its validity 
						if (!field.equals("title") || c.getTitle().equals(value))
							l.notify(c.getTitle(), c.getContract(), c.getKey());
					}
				}
			}
		};
		this.addAdvertisementListener(dl);
		
		// Search in remote adverts
		this.search(field, value, new SearchListener<EstablisherAdvertisementInterface>() {
			@Override
			public void notify(Collection<EstablisherAdvertisementInterface> adverts) {
				for(EstablisherAdvertisementInterface adv : adverts) {
					l.notify(adv.getTitle(), adv.getContract(), adv.getKey());
				}
			}
		});
		
		// Search in local adverts
		try {
			Enumeration<net.jxta.document.Advertisement> adverts = pg.getDiscoveryService().getLocalAdvertisements(DiscoveryService.ADV, field, value);
			while (adverts.hasMoreElements()){
				AdvertisementBridge adv = (AdvertisementBridge) adverts.nextElement();
				if (adv.getAdvertisement().getClass().equals(EstablisherAdvertisement.class)){
					EstablisherAdvertisementInterface c = (EstablisherAdvertisementInterface) adv.getAdvertisement();
					// If the field on which to listen is the title, then we check its validity 
					if (!field.equals("title") || c.getTitle().equals(value)){
						l.notify(c.getTitle(), c.getContract(), c.getKey());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Add listeners in the hashmap to be able to delete it and enable it
		establisherServiceListeners.put(listenerId, new ListenerWithParam(value, l));
		advertisementListeners.put(listenerId, dl);
	};

	// Remove any listener (message and Advertisement) with the given id
	@Override
	public void removeListener(String listenerId){
		if (advertisementListeners.containsKey(listenerId)){
			this.removeAdvertisementListener(advertisementListeners.get(listenerId));
			advertisementListeners.remove(listenerId);
		}
		if (establisherServiceListeners.containsKey(listenerId)){
			establisherServiceListeners.remove(listenerId);
		}else{
			super.removeListener(listenerId);
		}
	}
	
}
