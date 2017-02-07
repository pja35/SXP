package protocol.impl.sigma;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.Before;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import protocol.impl.sigma.ResponsesCCE;
import protocol.impl.sigma.ResponsesSchnorr;
import util.TestInputGenerator;
import protocol.impl.sigma.Fabric;

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

