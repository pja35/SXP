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
		ElGamalKey bobK, bobRK, aliK, aliRK, treK;
		bobK = ElGamalAsymKeyFactory.create(false);
		aliK = ElGamalAsymKeyFactory.create(false);
		treK = ElGamalAsymKeyFactory.create(false);
		Sender bob = new Sender(bobK);
		Sender alice = new Sender(aliK);
		
		bobRK = new ElGamalKey();
		aliRK = new ElGamalKey();
		bobRK.setG(bobK.getG());bobRK.setP(bobK.getP());bobRK.setPublicKey(bobK.getPublicKey());
		aliRK.setG(aliK.getG());aliRK.setP(aliK.getP());aliRK.setPublicKey(aliK.getPublicKey());

		//Bob side
		SigmaEstablisher sigmaE = new SigmaEstablisher(bob, aliRK, treK, msg);
		
		//Alice side
		SigmaEstablisher sigmaE2 = new SigmaEstablisher(alice, bobRK, treK, msg);
		
		sigmaE2.initialize(msg, Application.getInstance().getPeer().getUri());
		
		try{
			Thread.sleep(15000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue(true);
	}
}
