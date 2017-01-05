package network.impl;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import crypt.api.hashs.Hasher;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.factories.HasherFactory;
import model.entity.Item;
import model.entity.User;
import network.api.EstablisherService;
import network.api.Messages;
import network.api.Peer;
import network.api.ServiceListener;
import network.api.service.Service;
import network.factories.PeerFactory;
import network.impl.jxta.JxtaEstablisherService;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

import protocol.impl.SigmaEstablisher;
import protocol.impl.sigma.PCSFabric;

public class EstablisherTest {

	private Peer peer1;
	private Peer peer2;
	
	private EstablisherService establisherService1;
	private EstablisherService establisherService2;
	
	public Peer getPeer1() {
		return peer1;
	}
	public void setPeer1(Peer peer1) {
		this.peer1 = peer1;
	}
	
	public Peer getPeer2() {
		return peer2;
	}
	public void setPeer2(Peer peer2) {
		this.peer2 = peer2;
	}
	
	
	public void init()
	{
		//peer creation
		setPeer1(PeerFactory.createDefaultAndStartPeerForTest());
		setPeer2(PeerFactory.createDefaultAndStartPeerForTest());
		System.out.println("\n");
		
		//User creation
		final User u1 = new User();
		u1.setNick("User 1 ");
		u1.setId("1");

		final User u2 = new User();
		u2.setNick("User 2");
		u2.setId("2");
		
		//Establisher service setup
		setEstablisherService1((EstablisherService)getPeer1().getService("establisher"));
		setEstablisherService2((EstablisherService)getPeer2().getService("establisher"));
		

		
//		SigmaEstablisher sigmaE = new SigmaEstablisher();
//		sigmaE.initialize("Hello");
//		sigmaE.sign();
//		PrivateContractSignature pCS = sigmaE.getPrivateCS();
//		String pcs = sigmaE.getPcs();
		
		
		getEstablisherService1().addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				System.out.println("u1");
				System.out.println(messages.getMessage("title"));
				Integer m1 = new Integer(messages.getMessage("promI"));
				System.out.println(m1);
				String msg = String.valueOf(m1 + 1);
				if (m1 < 6) {
					System.out.println("u1");
					getEstablisherService1().sendPromI("Prom"+msg, getPeer2().getUri(), msg, getPeer1().getUri());
					System.out.println("\n");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}, getPeer2().getUri());
		
		getEstablisherService2().addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				System.out.println("u2");
				System.out.println(messages.getMessage("title"));
				Integer m1 = new Integer(messages.getMessage("promI"));
				System.out.println(m1);
				String msg = String.valueOf(m1 + 1);
				if (m1< 6) {
					System.out.println("u2");
					getEstablisherService2().sendPromI("Prom"+msg, getPeer1().getUri(), msg, getPeer2().getUri());
					System.out.println("\n");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}, getPeer1().getUri());
		
		getEstablisherService2().sendPromI("Prom1", getPeer2().getUri(), "1", getPeer1().getUri());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("User1 : "+u1.getId());
		System.out.println("User2 : "+u2.getId());		
		System.out.println("Peer1 : "+getPeer1().getUri());
		System.out.println("Peer2 : "+getPeer2().getUri());
	}
	@Test
	public void test() 
	{
		init();
	}
	public EstablisherService getEstablisherService1() {
		return establisherService1;
	}
	public void setEstablisherService1(EstablisherService establisherService) {
		this.establisherService1 = establisherService;
	}
	public EstablisherService getEstablisherService2() {
		return establisherService2;
	}
	public void setEstablisherService2(EstablisherService establisherService) {
		this.establisherService2 = establisherService;
	}
	
}
