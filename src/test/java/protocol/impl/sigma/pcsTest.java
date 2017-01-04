package protocol.impl.sigma;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import controller.tools.JsonTools;
import controller.tools.rKDeserializer;
import controller.tools.rKSerializer;
//import controller.tools.JsonTools;
import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import model.entity.Item;

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
		
		Or m = pcs.getPcs();
		assertTrue(m.Verifies(resEncrypt2));
		
		
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		String a = json.toJson(m, true);
		System.out.println(a);
		Or mm = json.toEntity(a,true);
		String aa = json.toJson(mm, true);
		System.out.println(aa);
		
				
		System.out.println(mm.Verifies(resEncrypt2));
	}
}
