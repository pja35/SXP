package protocol.impl.sigma;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

import crypt.impl.signatures.ElGamalSignature;
import util.TestInputGenerator;

/**
 * ElGamalSign unit test
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class ElGamalSignTest {
	private BigInteger r;
	private BigInteger s;
	ElGamalSignature sign;
	
	@Before
	public void instantiate(){
		r = TestInputGenerator.getRandomBigInteger(100);
		s = TestInputGenerator.getRandomBigInteger(100);
		sign = new ElGamalSignature(r, s);
	}
		
	@Test
	public void getterTest() {
		assertTrue(r.equals(sign.getR()));
		assertTrue(s.equals(sign.getS()));
	}
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	
	@Test
	public void toStringTest(){
		String outRes = "<signR>" + r.toString(16) + "</signR>" + "<signS>" + s.toString(16) + "</signS>";
		System.setOut(new PrintStream(outContent));
		System.out.print(sign);
		assertEquals(outRes, outContent.toString());
		System.setOut(new PrintStream(System.out));		
	}
}

