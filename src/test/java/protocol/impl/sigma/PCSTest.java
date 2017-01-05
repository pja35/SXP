package protocol.impl.sigma;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import protocol.impl.sigma.Or;
import protocol.impl.sigma.PCSFabric;
import protocol.impl.sigma.ResEncrypt;
import protocol.impl.sigma.Sender;

public class PCSTest {
	
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
		PCSFabric pcsf = new PCSFabric(bob, resEncrypt, aliceK, trentK);
		
		//Alice test the message sent by Bob on Trent public key
		ResEncrypt resEncrypt2 = alice.Encryption(buffer, trentK);
		
		//Get the Signature in the pcs
		Or m = pcsf.getPcs();
		
		//Alice checks the signature
		assertTrue(m.Verifies(resEncrypt2));
	}
}
