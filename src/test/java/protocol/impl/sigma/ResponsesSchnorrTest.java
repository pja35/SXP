package protocol.impl.sigma;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import protocol.impl.sigma.Fabric;
import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import protocol.impl.sigma.ResponsesSchnorr;

/**
 * ResponsesSchnorr unit test
 * @author denis.arrivault[@]univ-amu.fr
 */
public class ResponsesSchnorrTest {

	private ElGamalKey key;
	private ElGamalKey badKey;
	private ResponsesSchnorr response;
	
	@Before
	public void instantiate(){
		key = ElGamalAsymKeyFactory.create(false);
		response = (new Fabric()).SendResponseSchnorrFabric(key);
		badKey = ElGamalAsymKeyFactory.create(false);
	}
	
	@Test
	public void verifyTest() {
		assertTrue(response.Verifies(key, null));
		assertFalse(response.Verifies(badKey, null));
	}

	
}

