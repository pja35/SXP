package protocol.impl.sigma;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import util.TestInputGenerator;


/**
 * And unit test
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class AndTest {
	private Receiver receiver;
	private ResEncrypt resEncrypt;
	private HashMap <Responses,ElGamalKey> rK  = new HashMap <>();
	private byte[] message;
	Trent trent;
	private And and;
	
	@Before
	public void instantiate(){
		receiver = new Receiver();
		ElGamalKey senderKey = ElGamalAsymKeyFactory.create(false);
		ElGamalKey trentKey = ElGamalAsymKeyFactory.create(false);		
		Sender sender = new Sender(senderKey);
		message = TestInputGenerator.getRandomBytes(100);
		resEncrypt = sender.Encryption(message, trentKey);
		trent = new Trent(trentKey);
		ResponsesSchnorr responseSchnorr = sender.SendResponseSchnorr(message);		
		ResponsesCCE responseCCE = sender.SendResponseCCE(message, trentKey);
		rK = new HashMap<Responses, ElGamalKey>();
		rK.put(responseSchnorr, senderKey);
		rK.put(responseCCE, trentKey);
		and = new And(receiver, rK, resEncrypt, responseSchnorr, responseCCE); 
	}
	
	
	@Test
	public void trueVerifyTest() {
		assertTrue(and.Verifies(true));
		assertTrue(and.Verifies(false));		
	}
	
	@Test
	public void falseVerifyTest() {		
		ResponsesCCD responseCCD = trent.SendResponse(resEncrypt);
		rK.put(responseCCD, ElGamalAsymKeyFactory.create(false));
		and = new And(receiver, rK, resEncrypt, responseCCD);
		assertFalse(and.Verifies(false));
		
	}
	
	@Test
	public void falseVerifyTest2() {		
		for (Responses res : rK.keySet()) {
		    res.setChallenge(TestInputGenerator.getRandomBigInteger(100));
		}
		assertFalse(and.Verifies(true));
		assertFalse(and.Verifies(false));	
	}
}

