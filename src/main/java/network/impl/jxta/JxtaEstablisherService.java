/**
 * 
 */
package network.impl.jxta;

import java.util.Enumeration;

import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.pipe.PipeMsgEvent;
import network.api.EstablisherService;
import network.api.EstablisherServiceListener;
import network.api.advertisement.EstablisherAdvertisementInterface;
import network.impl.advertisement.EstablisherAdvertisement;
import network.impl.messages.EstablisherMessage;

/**
 * @author Nathanaël EON
 *
 */
public class JxtaEstablisherService extends JxtaService implements EstablisherService{
	public static final String NAME = "establisher";
	
	public JxtaEstablisherService ()
	{
		this.name = NAME;
	}
	
	
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
	
	/**
	 * Method called when a message is caught in the pipe
	 */
	@Override
	public void pipeMsgEvent(PipeMsgEvent event) {
		super.pipeMsgEvent(event);
	}
	
	@Override
	public void listens(String field, String value, final EstablisherServiceListener l){
		this.addAdvertisementListener(new DiscoveryListener(){
			@Override
			public void discoveryEvent(DiscoveryEvent event){
				Enumeration<net.jxta.document.Advertisement> adverts = event.getResponse().getAdvertisements();
				while (adverts.hasMoreElements()){
					AdvertisementBridge adv = (AdvertisementBridge) adverts.nextElement();
					if (adv.getAdvertisement().getClass().equals(EstablisherAdvertisement.class)){
						EstablisherAdvertisementInterface c = (EstablisherAdvertisementInterface) adv.getAdvertisement();
						l.notify(c);
					}
				}
			}
		});
	};
}
