package crypt.impl.hashs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import crypt.api.hashs.HasherTest;
import crypt.factories.HasherFactory;


/**
 * SHA256Hasher unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
@RunWith(value = Parameterized.class)
public class SHA256HasherTest {
	
	private HasherTest baseTest;
	

	public SHA256HasherTest(HasherTest baseTest) {
		this.baseTest = baseTest;
	}

	@Parameters
	public static Object[] data() {
		SHA256Hasher hasherNoSalt = new SHA256Hasher();
		byte[] salt = HasherFactory.generateSalt();
		SHA256Hasher hasherSalt = new SHA256Hasher();
		hasherSalt.setSalt(salt);
		return new Object[] {new HasherTest(hasherNoSalt), new HasherTest(hasherSalt)};
	}

	@Test
	public void testDeterminism() {
		baseTest.testDeterminism();
	}


	@Test
	public void testForCollisions() {
		baseTest.testForCollisions();
	}

	@Test
	public void testHashLength() {
		baseTest.testHashLength();
	}

	@Test
	public void testHashableHash() {
		baseTest.testHashableHash();
	}

	@Test
	public void testSaltEffect() {		
		baseTest.testSaltEffect();
	}
}

