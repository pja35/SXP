package protocol.impl.sigma;

import org.junit.Test;

import model.entity.ElGamalKey;
import crypt.factories.ElGamalAsymKeyFactory;

import static org.junit.Assert.*;

public class pcsTest {
	
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
		
		//Encrypte le message avec la clé publique de Trent
		ResEncrypt resEncrypt = bob.Encryption(buffer, trentK);
		
		//Créé la PCS
		PrivateContractSignature pcs = new PrivateContractSignature(bob, resEncrypt, aliceK, trentK);
		
		//Alice test the message sent by Bob on Trent public key
		ResEncrypt resEncrypt2 = alice.Encryption(buffer, trentK);
		assertTrue(pcs.getPcs().Verifies(resEncrypt2));
	}
}
