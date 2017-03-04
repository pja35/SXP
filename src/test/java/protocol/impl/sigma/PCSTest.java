package protocol.impl.sigma;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;

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
		Or pcs = (new PCS(bob, aliceK, trentK)).getPcs(buffer);
		
		//Alice checks the signature
		PCS pcsf = new PCS(alice, bobK, trentK);
		assertTrue(pcsf.PCSVerifies(pcs, buffer));
		
		buffer = "c".getBytes();
		assertFalse(pcsf.PCSVerifies(pcs, buffer));
	}
}
