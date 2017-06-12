package protocol.impl.sigma;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import model.entity.sigma.ResEncrypt;
import model.entity.sigma.ResponsesCCE;
import model.entity.sigma.ResponsesSchnorr;
import util.TestInputGenerator;

/**
 * Fabric unit test
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class FabricTest {

	byte[] input;
	Fabric fabric;
	ElGamalKey key;
	ResEncrypt res;

	@Before
	public void instantiate(){
		input = TestInputGenerator.getRandomBytes(100);
		fabric = new Fabric();
		key = ElGamalAsymKeyFactory.create(false);
		ElGamal elGamal = new ElGamal(key);
		ElGamalEncrypt encrypt = elGamal.encryptForContract(input);
		res = new ResEncrypt(encrypt.getU(), encrypt.getV(), input);
	}
	
	@Test
	public void sendResponsesTest() {
		ResponsesSchnorr schnorr = fabric.SendResponseSchnorrFabric(key);
		ResponsesCCE cce = fabric.SendResponseCCEFabric(res, key);
		assertTrue(schnorr.Verifies(key, res));
		assertTrue(cce.Verifies(key, res));
	}
}

