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
import network.api.advertisement.ContractAdvertisementInterface;

/**
 * Main class
 * @author Nathanaël EON
 *
 */

public class EstablisherTest {
	
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
		ContractAdvertisementInterface cadv = AdvertisementFactory.createContractAdvertisement();
		cadv.setTitle("Un Contrat");
		cadv.publish(peer);
		
		// Listener on establisher events
		final EstablisherService establisher =(EstablisherService) peer.getService(EstablisherService.NAME);
		establisher.addListener(new EstablisherListener() {
			@Override
			public void notify(Messages messages) {
				super.notify(messages);
				Integer m1 = new Integer(messages.getMessage("promI"));
				String msg = String.valueOf(m1 + 1);
				if (m1<6) {
					establisher.sendPromI("Contrat "+msg, "test", msg, messages.getMessage("source"));
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
		
		establisher.addListener(new EstablisherListener() {
			@Override
			public void notify(Messages messages) {
				super.notify(messages);
				Integer m1 = new Integer(messages.getMessage("promI"));
				String msg = String.valueOf(m1 + 1);
				if (m1<6) {
					establisher.sendPromI("Contrat "+msg, "test2", msg, messages.getMessage("source"));
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
		establisher.search("title", null, new SearchListener<ContractAdvertisementInterface>() {
			@Override
			public void notify(Collection<ContractAdvertisementInterface> result) {
				ArrayList<String> uids = new ArrayList<>();
				for(ContractAdvertisementInterface i: result) {
					uids.add(i.getSourceURI());
				}
				establisher.sendPromI("Contrat 1", "test2", "1", uids.toArray(new String[1]));
			}
			
		});
	}
	
	@Test
	public void test() {
	}
}
