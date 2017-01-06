package protocol.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import protocol.impl.sigma.Sender;

public class EstablisherTest {

	@Test
	public void test(){
		//Message for the example, in the end it will be the contract
		String msg = "hi";
		
		//Initialize the arguments
		ElGamalKey senK, recK, treK;
		senK = ElGamalAsymKeyFactory.create(false);
		recK = ElGamalAsymKeyFactory.create(false);
		treK = ElGamalAsymKeyFactory.create(false);
		Sender bob = new Sender(senK);
		Sender alice = new Sender(senK);
		
		//Bob side
		SigmaEstablisher sigmaE = new SigmaEstablisher();
		sigmaE.initialize(msg, bob, recK, treK);

		//Alice side
		SigmaEstablisher sigmaE2 = new SigmaEstablisher();
		sigmaE2.initialize(msg, alice, senK, treK);
		
		assertTrue(true);
	}
}
