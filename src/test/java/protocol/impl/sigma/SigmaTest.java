package protocol.impl.sigma;


import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import crypt.impl.key.ElGamalAsymKey;
import model.entity.ElGamalKey;
import protocol.impl.sigma.Masks;
import protocol.impl.sigma.Receiver;
import protocol.impl.sigma.ResEncrypt;
import protocol.impl.sigma.ResponsesCCE;
import protocol.impl.sigma.ResponsesSchnorr;
import protocol.impl.sigma.Sender;
import protocol.impl.sigma.Trent;
import protocol.impl.sigma.Utils;

import static org.junit.Assert.*;

import java.math.BigInteger;

public class SigmaTest {
	
	@Test
	public void test() {
		
		ElGamalKey bobK, aliceK, trentK;
		bobK = ElGamalAsymKeyFactory.create(false);
		aliceK = ElGamalAsymKeyFactory.create(false);
		trentK = ElGamalAsymKeyFactory.create(false);
		Sender Bob = new Sender(bobK);
		Sender Alice = new Sender(aliceK);
		
		Receiver AliceR = new Receiver();
		
		Trent Trent = new Trent(trentK);
		
		
		String message = "Bonjour toi !";
		byte[] buffer = message.getBytes();
		
		// Bob encrypte le message pour Trent
		ResEncrypt resEncrypt = Bob.Encryption(buffer, Trent.getKey());
		
		// Bob cree le masque Schnorr et le Challenge 
		Masks mask = Bob.SendMasksSchnorr();
		BigInteger challenge = Bob.SendChallenge(mask, resEncrypt.getM());
		
		// Bob fabrique les reponses pour charlie
		ResponsesSchnorr resSchnorrF = Bob.SendResponseSchnorrFabric(Alice.getKeys());
		ResponsesCCE resCCEF = Bob.SendResponseCCEFabric(resEncrypt, Trent.getKey());
		
		// trouve le challenge de BOB
		BigInteger c0 = challenge.xor(resCCEF.getChallenge()).xor(resSchnorrF.getChallenge());
		
		BigInteger c = Utils.rand(160, Bob.getKeys().getP());
		BigInteger c1 = c0.xor(c);
		
		ResponsesCCE resCCE = Bob.SendResponseCCE(resEncrypt.getM(), Trent.getKey(),c1);
		

		
		assertTrue(AliceR.Verifies(resCCE, trentK, resEncrypt));
	}
	
}
