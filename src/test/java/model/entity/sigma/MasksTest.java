package model.entity.sigma;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import model.entity.sigma.Masks;
import util.TestInputGenerator;

/**
 * Masks unit test
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class MasksTest {
	private final static Logger log = LogManager.getLogger(MasksTest.class);
	private BigInteger a;
	private BigInteger aBis;
	Masks masks;

	@Before
	public void instantiate(){
		a = TestInputGenerator.getRandomBigInteger(100);
		aBis = TestInputGenerator.getRandomBigInteger(100);
		masks = new Masks(a, aBis);
	}
	
	@Test
	public void getterSetterTest() {
		
		assertTrue(masks.getA().equals(a));
		assertTrue(masks.getaBis().equals(aBis));

		a = TestInputGenerator.getRandomBigInteger(100);
		aBis = TestInputGenerator.getRandomBigInteger(100);
		
		masks.setA(aBis);
		masks.setaBis(a);

		assertTrue(masks.getA().equals(aBis));
		assertTrue(masks.getaBis().equals(a));

		log.debug(masks);
	}
}

