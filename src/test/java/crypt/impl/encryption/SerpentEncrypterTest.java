package crypt.impl.encryption;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import util.TestInputGenerator;

/**
 * SerpentEncrypter unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
@RunWith(value = Parameterized.class)
public class SerpentEncrypterTest {
	@Rule public ExpectedException exception = ExpectedException.none();

	private String plainText;
	private String password;

	public SerpentEncrypterTest(String plainText, String password) {
		this.plainText = plainText;
		this.password = password;
	}

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{ TestInputGenerator.getRandomIpsumText(), TestInputGenerator.getRandomPwd() }, 
			{ TestInputGenerator.getRandomIpsumText(), TestInputGenerator.getRandomPwd() },
			{ TestInputGenerator.getRandomIpsumText(), TestInputGenerator.getRandomPwd() }, 
			{ TestInputGenerator.getRandomIpsumText(), TestInputGenerator.getRandomPwd() }  
		});
	}

	@Test
	public void testEncryptExceptions1() throws RuntimeException {
		SerpentEncrypter encrypter = new SerpentEncrypter();
		exception.expect(RuntimeException.class);
		exception.expectMessage("key not defined");
		encrypter.encrypt(plainText.getBytes());
	}
	
	@Test
	public void testEncryptExceptions2() throws RuntimeException {
		SerpentEncrypter encrypter = new SerpentEncrypter();
		exception.expect(RuntimeException.class);
		exception.expectMessage("key not defined");
		encrypter.decrypt(new byte[1]);
	}
	
	@Test
	public void testEncrypt() {
		SerpentEncrypter encrypter = new SerpentEncrypter();
		encrypter.setKey(password);
		byte[] cypher = encrypter.encrypt(plainText.getBytes());
		String plain = new String(encrypter.decrypt(cypher));
		assertEquals(plainText, plain);
	}
}
