package protocol.impl.sigma;


import org.junit.Before;
import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import protocol.impl.sigma.ResponsesSchnorr;
import protocol.impl.sigma.Sender;
import util.TestInputGenerator;

import static org.junit.Assert.*;

import java.math.BigInteger;

/**
 * Sender unit test
 * @author denis.arrivault[@]univ-amu.fr
 */
public class SenderReceiverTest {
	Sender sender;
	Receiver receiver;
	ElGamalKey senderKey;
	ElGamalKey receiverKey;
	ResEncrypt encryptMessage;
	byte[] message;
	
	@Before
	public void instantiate(){
		senderKey = ElGamalAsymKeyFactory.create(false);
		receiverKey = ElGamalAsymKeyFactory.create(false);
		sender = new Sender(senderKey);
		message = TestInputGenerator.getRandomBytes(100);
		encryptMessage = sender.Encryption(message, receiverKey);
		receiver = new Receiver();
	}
	
	@Test
	public void sendingTest() {
		
		String message = TestInputGenerator.getRandomIpsumText(10);
		byte[] buffer = message.getBytes();
		
		// Message encryption
		ResEncrypt resEncrypt = sender.Encryption(buffer, receiverKey);
		
		// Schnorr and Challenge creation 
		Masks mask = sender.SendMasksSchnorr();
		BigInteger challenge = sender.SendChallenge(mask, resEncrypt.getM());
		
		// Response creation
		ResponsesSchnorr resSchnorrF = sender.SendResponseSchnorrFabric(receiverKey);
		ResponsesCCE resCCEF = sender.SendResponseCCEFabric(resEncrypt, receiverKey);
		
		// get sender challenge
		BigInteger c0 = challenge.xor(resCCEF.getChallenge()).xor(resSchnorrF.getChallenge());
		BigInteger c = Utils.rand(160, sender.getKeys().getP());
		BigInteger c1 = c0.xor(c);
		
		ResponsesCCE resCCE = sender.SendResponseCCE(resEncrypt.getM(), receiverKey,c1);
		
		assertTrue(receiver.Verifies(resCCE, receiverKey, resEncrypt));
	}
	
}
