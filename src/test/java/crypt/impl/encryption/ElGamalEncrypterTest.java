package crypt.impl.encryption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import util.TestInputGenerator;


/**
 * ElGamalEncrypter unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
@RunWith(value = Parameterized.class)
public class ElGamalEncrypterTest {
	@Rule public ExpectedException exception = ExpectedException.none();

	private String plainText;

	public ElGamalEncrypterTest(String plainText) {
		this.plainText = plainText;
	}

	@Parameters
	public static Object[] data() {
		return new Object[] {TestInputGenerator.getRandomIpsumText(),
				TestInputGenerator.getRandomIpsumText(5),
				TestInputGenerator.getRandomIpsumText(5)};
	}


	@Test
	public void testEncryptExceptions() throws RuntimeException {
		ElGamalEncrypter encrypter = new ElGamalEncrypter();
		exception.expect(RuntimeException.class);
		exception.expectMessage("key not defined");	
		encrypter.encrypt(plainText.getBytes());
	}

	@Test
	public void testDecryptExceptions() throws RuntimeException {
		ElGamalEncrypter encrypter = new ElGamalEncrypter();
		exception.expect(RuntimeException.class);
		exception.expectMessage("key not defined");	
		encrypter.decrypt(new byte[1]);
	}

	@Test
	public void test() {
		ElGamalKey key = ElGamalAsymKeyFactory.create(false);
		ElGamalEncrypter encrypter = new ElGamalEncrypter();
		encrypter.setKey(key);
		byte[] cypher = encrypter.encrypt(plainText.getBytes());
		String decrypted = new String(encrypter.decrypt(cypher));
		assertEquals(plainText, decrypted);
		assertNotEquals(plainText, cypher);
	}
}
