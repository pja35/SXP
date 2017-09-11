package model.entity.sigma;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import model.entity.sigma.And;
import model.entity.sigma.Or;
import model.entity.sigma.ResEncrypt;
import model.entity.sigma.Responses;
import model.entity.sigma.ResponsesCCD;
import model.entity.sigma.ResponsesCCE;
import model.entity.sigma.ResponsesSchnorr;
import protocol.impl.sigma.Sender;
import protocol.impl.sigma.Trent;
import util.TestInputGenerator;

/**
 * Or unit test
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class OrTest {
	private final int N = 1;
	private ResEncrypt[] encryptMessages = new ResEncrypt[N];
	private byte[][] messages  = new byte[N][];
	private And[] ands = new And[N];
	
	private BigInteger a;
	private Or or;
	
	
	@Before
	public void instantiate(){
		a = TestInputGenerator.getRandomBigInteger(32);
		
		for(int i=0; i<N; i++){
			ElGamalKey senderKey = ElGamalAsymKeyFactory.create(false);
			ElGamalKey trentKey = ElGamalAsymKeyFactory.create(false);
			
			
			
			Sender sender = new Sender(senderKey);
			messages[i] = TestInputGenerator.getRandomBytes(100);
			encryptMessages[i] = sender.Encryption(messages[i], trentKey);
			
			
			Trent trent = new Trent(trentKey);

			ResponsesSchnorr responseSchnorr;
			
			ResponsesCCE responseCCE = sender.SendResponseCCE(messages[i], trentKey);
			
			ResponsesCCD responseCCD = trent.SendResponse(encryptMessages[i]);
			
			if (i== N-1){
				Masks mask = sender.SendMasksSchnorr();
				BigInteger c = sender.SendChallenge(mask, messages[i]);
				for (int k=0; k<N-1; k++)
					for (Responses r : ands[k].responses)
						c = c.xor(r.getChallenge());
				c = c.xor(responseCCE.getChallenge());
				c = c.xor(responseCCD.getChallenge());
				responseSchnorr = sender.SendResponseSchnorr(mask, c);
				a = mask.getA();
			}
			else{
				responseSchnorr = sender.SendResponseSchnorr(messages[i]);
			}
			
			HashMap<Responses, ElGamalKey> rK = new HashMap<Responses, ElGamalKey>();

			rK.put(responseSchnorr, senderKey);
			rK.put(responseCCE, trentKey);
			rK.put(responseCCD, trentKey);
			ands[i] = new And(rK, encryptMessages[i], responseSchnorr, responseCCE, responseCCD);
		}
				
		or = new Or(a, ands);
	}
	
	@Test
	public void getterSetterTest(){
		assertTrue(or.getA().equals(a));
		a = TestInputGenerator.getRandomBigInteger(100);
		or.setA(a);
		assertTrue(or.getA().equals(a));
	}

	@Test
	public void goodVerifyTest() {
		for(int i=0; i<N; i++){
			assertTrue(or.Verifies(encryptMessages[i].getM()));
		}
	}
	
	@Test
	public void badVerifyTest() {
		for(int i=0; i<N; i++){
			ResEncrypt randomEncrypt = new ResEncrypt(TestInputGenerator.getRandomBigInteger(32),
					TestInputGenerator.getRandomBigInteger(32), TestInputGenerator.getRandomBytes(100));
			assertFalse(or.Verifies(randomEncrypt.getM()));
		}
	}
}

