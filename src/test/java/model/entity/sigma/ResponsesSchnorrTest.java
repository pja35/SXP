package model.entity.sigma;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import model.entity.sigma.ResponsesSchnorr;
import protocol.impl.sigma.Fabric;

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
	public void equalTest(){
		ResponsesSchnorr response2 = new ResponsesSchnorr();
		ResponsesSchnorr response3 = (new Fabric()).SendResponseSchnorrFabric(key);
		assertFalse(response.equals(1));
		assertFalse(response.equals(response2));
		assertFalse(response.equals(response3)); //Different masks
		assertTrue(response.equals(response));
	}
	
	@Test
	public void verifyTest() {
		assertTrue(response.Verifies(key, null));
		assertFalse(response.Verifies(badKey, null));
	}

	
}

