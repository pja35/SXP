package protocol.impl.sigma;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;

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
		
		//Get the Signature in the pcs
		Or m = pcs.getPcs();
		
		//Alice checks the signature
		assertTrue(m.Verifies(resEncrypt2));
		
		//Transform the signature into string to send it upon the network
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		String a = json.toJson(m, true);
		
		//Get the signature back the signature from the string and test it
		Or mm = json.toEntity(a,true);
		assertTrue(mm.Verifies(resEncrypt2));
	}
}
