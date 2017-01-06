package protocol.impl.sigma;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import protocol.impl.sigma.Or;
import protocol.impl.sigma.PCSFabric;
import protocol.impl.sigma.Sender;

public class PCSTest {
	
	@Test
	public void test(){

		ElGamalKey bobK, aliceK, trentK;
		bobK = ElGamalAsymKeyFactory.create(false);
		aliceK = ElGamalAsymKeyFactory.create(false);
		trentK = ElGamalAsymKeyFactory.create(false);
		
		Sender bob = new Sender(bobK);
		Sender alice = new Sender(aliceK);
		
		String message="coucou !";
		byte[] buffer = message.getBytes();
		
		//Create the PCS
		Or pcs = (new PCSFabric(bob, aliceK, trentK)).getPcs(buffer);
		
		//Alice checks the signature
		PCSFabric pcsf = new PCSFabric(alice, bobK, trentK);
		assertTrue(pcsf.PCSVerifies(pcs, buffer));
	}
}
