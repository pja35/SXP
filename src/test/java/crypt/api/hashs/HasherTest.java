package crypt.api.hashs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import crypt.factories.HasherFactory;
import util.TestInputGenerator;


/**
 * Hasher api unit tests via HasherFactory.createDefaultHasher()
 * @author denis.arrivault[@]univ-amu.fr
 */
@RunWith(value = Parameterized.class)
public class HasherTest {

	public int maxMessageLength = 1000;
	public int nbMessages = 1000;
	public float maxCollisionRatio = 1f / 1000000;
	
	private Hasher hasher;
	
	public class ToyHashable implements Hashable {
		private byte[] hashableData = TestInputGenerator.getRandomBytes(maxMessageLength);

		@Override
		public byte[] getHashableData() {
			return hashableData;
		}
	}

	@Parameters
	public static Object[] data() {
		byte[] salt = HasherFactory.generateSalt();
		Hasher hasherNoSalt = HasherFactory.createDefaultHasher();
		Hasher hasherSalt = HasherFactory.createDefaultHasher();
		hasherSalt.setSalt(salt);
		return new Object[] {hasherNoSalt, hasherSalt};
	}
	
	
	public HasherTest(Hasher hasher) {
		this.hasher = hasher;
	}


	@Test
	public void testDeterminism() {
		byte[] message = TestInputGenerator.getRandomBytes(maxMessageLength);
		assertTrue(Arrays.equals(hasher.getHash(message), hasher.getHash(message)));
	}


	@Test
	public void testForCollisions() {
		int nbCollisions = 0;

		for (int i = 0; i < nbMessages; ++i) {
			byte[] message1 = TestInputGenerator.getRandomBytes(maxMessageLength);
			byte[] message2 = TestInputGenerator.getRandomBytes(maxMessageLength);
			if (Arrays.equals(message1, message2)){
				message2[0] = (byte) (message1[0] + 127); 
			}
			byte[] hash1 = hasher.getHash(message1);
			byte[] hash2 = hasher.getHash(message2);
			if (Arrays.equals(hash1, hash2))
				nbCollisions++;
		}
		assertTrue(nbCollisions <= (maxCollisionRatio * nbMessages));
	}

	@Test
	public void testHashLength() {
		for (int i = 0; i < 10; i++) {
			byte[] hash = hasher.getHash(TestInputGenerator.getRandomBytes(maxMessageLength));
			assertTrue(hash.length * Byte.SIZE == 256);
		}
	}

	@Test
	public void testHashableHash() {
		Hashable hashable = new ToyHashable();
		byte[] hash1 = hasher.getHash(hashable.getHashableData());
		byte[] hash2 = hasher.getHash(hashable);

		assertTrue(Arrays.equals(hash1, hash2));
	}

	@Test
	public void testSaltEffect() {		
		byte[] message = TestInputGenerator.getRandomBytes(maxMessageLength);
		byte[] hash1 = hasher.getHash(message);
		byte[] salt = HasherFactory.generateSalt();
		Hasher hasherSalt = HasherFactory.createDefaultHasher();
		hasherSalt.setSalt(salt);
		byte[] hash2 = hasherSalt.getHash(message);
		assertFalse(Arrays.equals(hash1, hash2));
	}

}

