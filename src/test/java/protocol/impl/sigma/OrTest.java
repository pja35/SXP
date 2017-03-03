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
 * Or unit test
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class OrTest {
	private Receiver receiver;
	
	private final int N = 1;
	private ResEncrypt[] encryptMessages = new ResEncrypt[N];
	private byte[][] messages  = new byte[N][];
	private And[] ands = new And[N];
	
	private BigInteger a;
	private Or or;

	@Before
	public void instantiate(){
		receiver = new Receiver();
		a = TestInputGenerator.getRandomBigInteger(32);
		
		for(int i=0; i<N; i++){
			ElGamalKey senderKey = ElGamalAsymKeyFactory.create(false);
			ElGamalKey trentKey = ElGamalAsymKeyFactory.create(false);
			
			
			
			Sender sender = new Sender(senderKey);
			messages[i] = TestInputGenerator.getRandomBytes(100);
			encryptMessages[i] = sender.Encryption(messages[i], trentKey);
			
			
			Trent trent = new Trent(trentKey);

			ResponsesSchnorr responseSchnorr = sender.SendResponseSchnorr(messages[i]);
			
			ResponsesCCE responseCCE = sender.SendResponseCCE(messages[i], trentKey);
			
			ResponsesCCD responseCCD = trent.SendResponse(encryptMessages[i]);
			
			HashMap<Responses, ElGamalKey> rK = new HashMap<Responses, ElGamalKey>();

			rK.put(responseSchnorr, senderKey);
			rK.put(responseCCE, trentKey);
			rK.put(responseCCD, trentKey);
			ands[i] = new And(receiver, rK, encryptMessages[i], responseSchnorr, responseCCE, responseCCD);
		}
				
		or = new Or(receiver, a, ands);
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
			assertTrue(or.Verifies(encryptMessages[i]));
		}
	}
	
	@Test
	public void badVerifyTest() {
		for(int i=0; i<N; i++){
			ResEncrypt randomEncrypt = new ResEncrypt(TestInputGenerator.getRandomBigInteger(32),
					TestInputGenerator.getRandomBigInteger(32), TestInputGenerator.getRandomBytes(100));
			assertTrue(or.Verifies(randomEncrypt));
			// Design Annotation : Is that the normal behavior ???
		}
	}
}

