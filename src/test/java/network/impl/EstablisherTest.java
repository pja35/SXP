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
import protocol.impl.sigma.PrivateContractSignature;

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
		

		
		SigmaEstablisher sigmaE = new SigmaEstablisher();
		sigmaE.initialize("Hello");
		sigmaE.sign();
		final PrivateContractSignature pCS = sigmaE.getPrivateCS();
		final String pcs = sigmaE.getPcs();
		
		
		getEstablisherService1().addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				System.out.println("u1");
				System.out.println(messages.getMessage("title"));
				Integer pcs2 = new Integer(messages.getMessage("promI"));
				System.out.println(pcs2);
				String msg = String.valueOf(pcs2 + 1);
				if (pcs2 < 6) {
					System.out.println("u1");
					getEstablisherService1().sendPromI("Prom"+msg, u1.getId(), getPeer2().getUri(), msg, getPeer1().getUri());
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
		
		getEstablisherService2().addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				System.out.println("u2");
				System.out.println(messages.getMessage("title"));
				Integer pcs2 = new Integer(messages.getMessage("promI"));
				System.out.println(pcs2);
				String msg = String.valueOf(pcs2 + 1);
				if (pcs2 < 6) {
					System.out.println("u2");
					getEstablisherService2().sendPromI("Prom"+msg, u1.getId(), getPeer1().getUri(), msg, getPeer2().getUri());
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
		
		getEstablisherService2().sendPromI("Prom1", getPeer1().getUri(), getPeer1().getUri(), "1", getPeer2().getUri(), getPeer1().getUri());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		//Initializing listener
//		getEstablisherService2().addListener(new EstablisherListener(), u1.getId());
//		getEstablisherService1().addListener(new EstablisherListener(), u2.getId());
//		
//
//		getEstablisherService1().sendPromI(u1.getNick() + ", Prom 1", u2.getId(), this.getPeer2().getUri(), "hello", this.getPeer1().getUri());
//		System.out.println("\n");
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		getEstablisherService2().sendPromI(u2.getNick() + ", Prom 2", u1.getId(), getPeer1().getUri(), "", getPeer2().getUri());
//		System.out.println("\n");
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		getEstablisherService1().sendPromI(u1.getNick() + ", Prom 3", u2.getId(), getPeer2().getUri(), pcs, getPeer1().getUri());
//		System.out.println("\n");
//		System.out.println("Wait ! Leavin ?");
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		getEstablisherService1().sendPromI(u1.getNick() + ", Prom 3", u2.getId(), this.getPeer2().getUri(), pcs, this.getPeer1().getUri());
//		System.out.println("\n");
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
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
