package protocol.impl.sigma;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import protocol.impl.sigma.Hasher;
import util.TestInputGenerator;

/**
 * Hasher unit test
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class HasherTest {
	private final static Logger log = LogManager.getLogger(HasherTest.class);
	@Rule public ExpectedException exception = ExpectedException.none();
	
	private String text;
	private String password;
	private byte[] textBytes;	
	private byte[] salt;
	private Hasher hash;
	
	@Before
	public void instantiate(){
		text = TestInputGenerator.getRandomIpsumText();
		password = TestInputGenerator.getRandomPwd(20);
		textBytes = text.getBytes();
		salt = TestInputGenerator.getRandomBytes(100);
		hash = new Hasher();
	}
	
	@Test
	public void simpleSHA256Test(){
		String textHash = Hasher.SHA256(text);
		String textBytesHash = Hasher.SHA256(textBytes);
		log.debug(textHash);
		log.debug(textBytesHash);
		assertTrue(textHash.equals(Hasher.SHA256(text)));
		assertTrue(textBytesHash.equals(Hasher.SHA256(textBytes)));
		assertTrue(textBytesHash.equals(textHash));
		assertEquals(64, textHash.length());
		assertEquals(64, textBytesHash.length());
	}
	
	@Test
	public void saltSHA256Test(){
		String textHash = Hasher.SHA256(textBytes, salt);
		log.debug(textHash);
		assertTrue(textHash.equals(Hasher.SHA256(textBytes, salt)));
		assertEquals(64, textHash.length());
		assertFalse(Hasher.SHA256(textBytes).equals(textHash));
	}
	
	@Test
	public void passwordShA256Test(){
		String saltStr = TestInputGenerator.getRandomAlphaWord(64);
		String pwdHash1 = Hasher.SHA256(password, saltStr);
		String pwdHash2 = Hasher.SHA256(password.getBytes(), saltStr.getBytes());
		log.debug(pwdHash1);
		log.debug(pwdHash2);
		assertTrue(pwdHash1.equals(pwdHash2));
	}
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	
	@Test
	public void mainShA256Test(){
		System.setOut(new PrintStream(outContent));
		Hasher.main(null);
		String testHash = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08\n";
		assertEquals(testHash, outContent.toString());
		System.setOut(new PrintStream(System.out));
	}

}