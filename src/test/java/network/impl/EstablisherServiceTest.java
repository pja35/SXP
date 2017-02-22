package network.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import controller.Application;
import network.factories.AdvertisementFactory;
import network.api.EstablisherService;
import network.api.Messages;
import network.api.Peer;
import network.api.SearchListener;
import network.api.ServiceListener;
import network.api.advertisement.EstablisherAdvertisementInterface;

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
		EstablisherAdvertisementInterface cadv = AdvertisementFactory.createsEstablisherAdvertisement();
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
	
	@Test
	public void test() {
	}
}
