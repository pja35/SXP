package protocol.impl.sigma;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;

public class PCSTest {
	
	static private ElGamalKey bobK, aliceK, trentK;
	static private Sender alice, bob;
	static private byte[] buffer;
	static private Or o;
	static private PCS pcs;
	
	@BeforeClass
	static public void settingUp(){

		bobK = ElGamalAsymKeyFactory.create(false);
		aliceK = ElGamalAsymKeyFactory.create(false);
		trentK = ElGamalAsymKeyFactory.create(false);
		
		bob = new Sender(bobK);
		alice = new Sender(aliceK);
		
		String message="coucou !";
		buffer = message.getBytes();
		
		//Create the PCS
		o = (new PCS(bob, aliceK, trentK)).getPcs(buffer);
		
		//Alice creates the signature
		pcs = new PCS(alice, bobK, trentK);
		pcs.getPcs(buffer);
	}
	
	/**
	 * Alice verifies her signature
	 */
	@Test
	public void TestAa(){
		assertTrue(pcs.PCSVerifies(buffer));
	}
	
	/**
	 * Alice verifies that her own signature doesn't match the buffer
	 */
	@Test
	public void TestAb(){
		System.out.println("---- Begining ----\n -->No need to worry, the following 3 lines are meant to be");
		assertFalse(pcs.PCSVerifies("c".getBytes()));
		System.out.println("---- END ----\n");
	}
	
	/**
	 * Alice verifies bob signature
	 */
	@Test
	public void TestBa(){
		assertTrue(pcs.PCSVerifies(o,buffer));
	}
	
	/**
	 * Alice verifies that her own signature doesn't match the buffer
	 */
	@Test
	public void TestBb(){
		System.out.println("---- Begining ----\n -->No need to worry, the following 3 lines are meant to be");
		assertFalse(pcs.PCSVerifies(o,"c".getBytes()));
		System.out.println("---- END ----");
	}
	
	
	
}
