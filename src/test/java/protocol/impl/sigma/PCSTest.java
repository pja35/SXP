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
		Or pcs = (new PCSFabric(bob, aliceK, trentK)).createPcs(buffer);
		
		//Alice checks the signature
		PCSFabric pcsf = new PCSFabric(alice, bobK, trentK);
		assertTrue(pcsf.PCSVerifies(pcs, buffer));
		
		buffer = "c".getBytes();
		assertFalse(pcsf.PCSVerifies(pcs, buffer));
	}
}
