package crypt.impl.signatures;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import model.entity.sigma.Or;
import model.entity.sigma.Responses;
import model.entity.sigma.SigmaSignature;
import util.TestInputGenerator;

public class SigmaSignatureTest {

	private static ElGamalKey trentK = ElGamalAsymKeyFactory.create(false);
	private static ElGamalKey senderK = ElGamalAsymKeyFactory.create(false);
	private static ElGamalKey receiverK = ElGamalAsymKeyFactory.create(false);
	
	@Test
	public void test(){
		SigmaSigner s = new SigmaSigner();
		s.setKey(senderK);
		s.setReceiverK(receiverK);	
		s.setTrentK(trentK);
		
		String data = TestInputGenerator.getRandomIpsumText();
		byte[] input = data.getBytes();
		SigmaSignature signature = s.sign(input);

		JsonTools<Or> json1 = new JsonTools<>(new TypeReference<Or>(){});
		JsonTools<Responses> json2 = new JsonTools<>(new TypeReference<Responses>(){});
		JsonTools<ElGamalKey> json3 = new JsonTools<>(new TypeReference<ElGamalKey>(){});
		assertTrue(signature.getParam("pcs").equals(json1.toJson(signature.getPcs(), true)));
		assertTrue(signature.getParam("rpcs").equals(json2.toJson(signature.getRpcs())));
		assertTrue(signature.getParam("trentK").equals(json3.toJson(signature.getTrentK())));
	}
}
