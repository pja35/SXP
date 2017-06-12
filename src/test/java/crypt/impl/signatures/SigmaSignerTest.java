package crypt.impl.signatures;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import model.entity.sigma.SigmaSignature;
import util.TestInputGenerator;

public class SigmaSignerTest {
	
	private static ElGamalKey trentK = ElGamalAsymKeyFactory.create(false);
	private static ElGamalKey senderK = ElGamalAsymKeyFactory.create(false);
	private static ElGamalKey receiverK = ElGamalAsymKeyFactory.create(false);
	
	@Test
	public void test(){
		SigmaSigner s = new SigmaSigner();
		s.setKey(senderK);
		s.setReceiverK(receiverK);	
		s.setTrentK(trentK);
		
		String data = TestInputGenerator.getRandomIpsumText();
		byte[] input = data.getBytes();
		SigmaSignature signature = s.sign(input);
		
		s.setReceiverK(senderK);
		assertTrue(s.verify(input, signature));
		
		s.setTrentK(null);
		assertTrue(s.verify(input, signature));
	}
}
