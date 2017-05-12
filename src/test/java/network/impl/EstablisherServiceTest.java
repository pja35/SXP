package network.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;

import org.junit.Test;

import controller.Application;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import network.api.EstablisherService;
import network.api.Messages;
import network.api.Peer;
import network.api.SearchListener;
import network.api.ServiceListener;
import network.api.advertisement.EstablisherAdvertisementInterface;
import network.factories.AdvertisementFactory;
import network.impl.advertisement.EstablisherAdvertisement;
import network.impl.jxta.AdvertisementBridge;

/**
 * !!!! This test cannot be launched as a junit test !!!!!
 * 	We need to start 2 peers which JXTA won't allow for 
 * 	a single app, thus, we need to test it manually by
 *	starting 2 applications. Down is the code for the "main"
 * 	method
 * @author Nathanaël EON
 *
 */

public class EstablisherServiceTest {
	
	
	
	////////// The first two methods are for message test ///////////////
	/*
	 * First launch, create a contract advertisement and set a listener for "test2" the other user
	 * This should be the content of the "main"
	 */ 
	public void init1()
	{
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
				System.out.println(m1 + " Contract : " + messages.getMessage("title"));
				
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
	
	
	/*
	 * Second launch, this will find the advertisement created by the first launch and send him a message
	 * Then there will be an exchange of messages
	 * Note that the port isn't the same
	 */
	public void init2()
	{
		new Application();
		Application.getInstance().runForTests(8080);
		
		final EstablisherService establisher =(EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);
		
		establisher.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				Integer m1 = new Integer(messages.getMessage("contract"));
				System.out.println(m1 + " Contract : " + messages.getMessage("title"));
				
				String msg = String.valueOf(m1 + 1);
				if (m1<6) {
					establisher.sendContract("Contract "+msg, "test2" , "test" , msg, messages.getMessage("source"));
				}
				try{
					Thread.sleep(1000);
				}catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}, "test");
		
		try{
			Thread.sleep(10000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		establisher.search("title", null, new SearchListener<EstablisherAdvertisementInterface>() {
			@Override
			public void notify(Collection<EstablisherAdvertisementInterface> result) {
				ArrayList<String> uids = new ArrayList<>();
				for(EstablisherAdvertisementInterface i: result) {
					uids.add(i.getSourceURI());
				}
				establisher.sendContract("Contract 1", "test2", "test", "1", uids.toArray(new String[1]));
			}
			
		});
	}
	
	
	////////// The next method is for advertisement listener test, you shall start it on two peers ///////////////
	// If the same machine is used, change the port of run for test
	public void initAdvert(){
		new Application();
		Application.getInstance().runForTests(8081);

		//Sending an advertisement
		final Peer peer=Application.getInstance().getPeer();
		EstablisherAdvertisementInterface cadv = AdvertisementFactory.createEstablisherAdvertisement();
		cadv.setTitle("Contract");
		cadv.setContract("1");
		cadv.setKey("KEY1");
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
						System.out.println(c.getKey() + "\n");
						
						Integer i = new Integer(c.getContract());
						

						try{
							Thread.sleep(2000);
						}catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						if (i<3){
							final Peer peer=Application.getInstance().getPeer();
							
							EstablisherAdvertisementInterface cadv = AdvertisementFactory.createEstablisherAdvertisement();
							cadv.setTitle("Contract");
							cadv.setContract(String.valueOf(i.intValue() + 1));
							cadv.setKey("KEY2");
							
							cadv.publish(peer);
						}
					}
				}
			}
		});
	}
	
	
	@Test
	public void test() {
	}
}
