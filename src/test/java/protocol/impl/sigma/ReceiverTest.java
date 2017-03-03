package protocol.impl.sigma;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import util.TestInputGenerator;

/**
 * Receiver unit test
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class ReceiverTest {
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
	public void singleResponseVerifyTest(){
		ResponsesSchnorr responseSchnorr = sender.SendResponseSchnorr(message);
		assertTrue(receiver.Verifies(responseSchnorr, senderKey, null));
		
		ResponsesCCE responseCCE = sender.SendResponseCCE(message, receiverKey);
		assertTrue(receiver.Verifies(responseCCE, receiverKey, encryptMessage));
	}
	
	@Test
	public void multipleResponseVerifyTest(){
		ResponsesSchnorr responseSchnorr = sender.SendResponseSchnorr(message);
		ResponsesCCE responseCCE = sender.SendResponseCCE(message, receiverKey);
		
		HashMap<Responses, ElGamalKey> rK = new HashMap<Responses, ElGamalKey>();
		rK.put(responseSchnorr, senderKey);
		rK.put(responseCCE, receiverKey);
		assertTrue(receiver.Verifies(true, rK, encryptMessage, responseSchnorr, responseCCE));
		
		And and = new And(receiver, rK, encryptMessage, responseSchnorr, responseCCE);
		assertTrue(receiver.Verifies(and, false));
		
		assertTrue(receiver.Verifies(new BigInteger("1"), encryptMessage, and));
	}
}

