package model.entity.sigma;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import model.entity.sigma.And;
import model.entity.sigma.ResEncrypt;
import model.entity.sigma.Responses;
import model.entity.sigma.ResponsesCCD;
import model.entity.sigma.ResponsesCCE;
import model.entity.sigma.ResponsesSchnorr;
import protocol.impl.sigma.Sender;
import protocol.impl.sigma.Trent;
import util.TestInputGenerator;


/**
 * And unit test
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class AndTest {
	private ResEncrypt resEncrypt;
	private HashMap <Responses,ElGamalKey> rK  = new HashMap <>();
	private byte[] message;
	Trent trent;
	private And and;
	
	@Before
	public void instantiate(){
		ElGamalKey senderKey = ElGamalAsymKeyFactory.create(false);
		ElGamalKey trentKey = ElGamalAsymKeyFactory.create(false);		
		Sender sender = new Sender(senderKey);
		trent = new Trent(trentKey);
		message = TestInputGenerator.getRandomBytes(100);
		resEncrypt = sender.Encryption(message, trentKey);
		ResponsesSchnorr responseSchnorr = sender.SendResponseSchnorr(message);		
		ResponsesCCE responseCCE = sender.SendResponseCCE(message, trentKey);
		rK = new HashMap<Responses, ElGamalKey>();
		rK.put(responseSchnorr, senderKey);
		rK.put(responseCCE, trentKey);
		and = new And(rK, resEncrypt, responseSchnorr, responseCCE); 
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
		and = new And(rK, resEncrypt, responseCCD);
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
	
	@Test
	public void toStringTest(){
		assertTrue(!(and.toString().equals("")));
	}
}

