package protocol.impl.sigma;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

import java.math.BigInteger;
import javax.xml.bind.DatatypeConverter;

import protocol.impl.sigma.Utils;
import util.TestInputGenerator;

/**
 * Utils unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class UtilsTest {
	@Rule public ExpectedException exception = ExpectedException.none();
	private final static Logger log = LogManager.getLogger(UtilsTest.class);
	private Utils utils;
	int bitLength;
	BigInteger p;
	byte[] b;
	
	@Before
	public void instantiate(){
		utils = new Utils();
		bitLength = TestInputGenerator.getRandomInt(2, 10);
		p = TestInputGenerator.getRandomNotNullBigInteger(bitLength);
		b = TestInputGenerator.getRandomBytes(10);
	}
		
	@Test
	public void randIllegalArgumentExceptionTest1(){
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("numBits must be non-negative");
		Utils.rand(-1, p);
	}
	
	@Test(timeout=1000)
	public void randTest(){
		BigInteger bir = Utils.rand(bitLength, p);
		assert(bir.compareTo(BigInteger.ONE) >= 0 && bir.compareTo(p) <= 0);
	}
	
	@Test(timeout=1000)
	public void randGetOneTest(){
		BigInteger un = BigInteger.ONE;
		BigInteger bir = Utils.rand(5, un);
		assert(bir.compareTo(BigInteger.ONE) == 0);
	}
	
	@Test
	public void toHexTest(){
		String bHex = Utils.toHex(b);
		log.debug(bHex);
		byte[] bb = DatatypeConverter.parseHexBinary(bHex);
		log.debug(TestInputGenerator.byteToString(b) + " =? " + TestInputGenerator.byteToString(bb));
		assertArrayEquals(bb, b);
	}
	
	@Test(timeout=1000)
	public void zeroRandTest(){
		BigInteger bir = Utils.rand(5, new BigInteger("00000"));
		assert(bir.compareTo(BigInteger.ZERO) == 0);
	}

}

