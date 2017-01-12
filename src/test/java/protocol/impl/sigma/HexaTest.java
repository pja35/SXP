package protocol.impl.sigma;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

import protocol.impl.sigma.Hexa;
import util.TestInputGenerator;

/**
 * Hexa unit test
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class HexaTest {
	private final static Logger log = LogManager.getLogger(HexaTest.class);
	@Rule public ExpectedException exception = ExpectedException.none();
	
	private String text;
	private byte[] bytes;
	Hexa hexa;


	@Before
	public void instantiate(){
		text = TestInputGenerator.getRandomIpsumText();
		bytes = TestInputGenerator.getRandomBytes(100);
		hexa = new Hexa();
	}
	
	@Test
	public void testExceptions() throws NullPointerException{
		exception.expect(NullPointerException.class);
	    Hexa.stringToHex(null);
	}
	
	@Test
	public void testByteToHex() {
		assertArrayEquals(bytes, Hexa.hexToBytes(Hexa.bytesToHex_UpperCase(bytes)));
		assertArrayEquals(bytes, Hexa.hexToBytes(Hexa.bytesToHex(bytes)));
	}
	
	@Test 
	public void stringToHex(){
		String hello = "hello";
		String hello_hex = "68656c6c6f";
		log.debug(Hexa.stringToHex(hello));
		assertTrue(Hexa.stringToHex(hello).equals(hello_hex));
		log.debug(Hexa.hexToString(hello_hex));
		assertTrue(Hexa.hexToString(Hexa.stringToHex(hello)).equals(hello));
		assertTrue(Hexa.hexToString(Hexa.stringToHex(text)).equals(text));
	}
}

