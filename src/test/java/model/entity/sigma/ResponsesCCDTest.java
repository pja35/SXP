package model.entity.sigma;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import model.entity.sigma.ResEncrypt;
import model.entity.sigma.ResponsesCCD;
import protocol.impl.sigma.Sender;
import protocol.impl.sigma.Trent;
import util.TestInputGenerator;

/**
 * ResponsesCCD unit test
 * @author denis.arrivault[@]univ-amu.fr
 */
public class ResponsesCCDTest {
	private byte[] msg;
	private ElGamalKey key;
	private ElGamalKey badKey;
	private ResponsesCCD response;
	ResEncrypt res;
	ResEncrypt badRes;
	
	@Before
	public void instantiate(){		
		key = ElGamalAsymKeyFactory.create(false);
		badKey = ElGamalAsymKeyFactory.create(false);
		msg = TestInputGenerator.getRandomBytes(100);
		Sender sender = new Sender(key);
		res = sender.Encryption(msg, key);
		badRes = sender.Encryption(msg, badKey);
		response = (new Trent(key)).SendResponse(res);
	}
	
	@Test
	public void equalTest(){
		ResponsesCCD response2 = new ResponsesCCD();
		ResponsesCCD response3 = (new Trent(key)).SendResponse(res);
		assertFalse(response.equals(1));
		assertFalse(response.equals(response2));
		assertFalse(response.equals(response3)); //Different masks
		assertTrue(response.equals(response));
	}
	
	@Test
	public void verifyTest() {
		assertTrue(response.Verifies(key, res));
		assertFalse(response.Verifies(badKey, res));
		assertFalse(response.Verifies(key, badRes));

	}
}

