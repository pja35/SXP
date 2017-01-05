package protocol.impl.sigma;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import protocol.impl.sigma.ResEncrypt;
import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import protocol.impl.sigma.Trent;
import util.TestInputGenerator;
import protocol.impl.sigma.ResponsesCCD;

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
	public void verifyTest() {
		assertTrue(response.Verifies(key, res));
		assertFalse(response.Verifies(badKey, res));
		assertFalse(response.Verifies(key, badRes));

	}
}

