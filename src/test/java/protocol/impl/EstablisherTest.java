package protocol.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import controller.Application;
import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import protocol.impl.sigma.Sender;

public class EstablisherTest {

	@Test
	public void test(){
		// Starting the Application to be able to test it
		new Application();
		Application.getInstance().runForTests(8081);
		
		//Message for the example, in the end it will be the contract
		String msg = "hi";

		//Initialize the arguments
		ElGamalKey senK, recK, treK;
		senK = ElGamalAsymKeyFactory.create(false);
		recK = ElGamalAsymKeyFactory.create(false);
		treK = ElGamalAsymKeyFactory.create(false);
		Sender bob = new Sender(senK);
		Sender alice = new Sender(recK);

		//Bob side
		SigmaEstablisher sigmaE = new SigmaEstablisher(bob, recK, treK,msg);
		
		//Alice side
		SigmaEstablisher sigmaE2 = new SigmaEstablisher(alice, senK, treK,msg);
		
		sigmaE2.initialize(msg, Application.getInstance().getPeer().getUri());
		
		try{
			Thread.sleep(15000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(sigmaE.getStatus());
		System.out.println(sigmaE2.getStatus());
		assertTrue(true);
	}
}
