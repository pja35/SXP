package model.entity;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import protocol.api.Wish;

/**
 * ElGamalContractEntity unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class ElGamalContractEntityTest {
	ElGamalContractEntity contract = new ElGamalContractEntity();

	@Test
	public void test() {
		for(Wish wish : Wish.values()){
			assertTrue(Wish.valueOf(wish.toString()).equals(wish));
			contract.setWish(wish);
			assertTrue(contract.getWish() == wish);
		}
		assertTrue(contract.getParties() == null);
		assertTrue(contract.getSignatures() == null);	
	}
}
