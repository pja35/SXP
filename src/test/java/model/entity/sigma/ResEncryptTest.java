package model.entity.sigma;

import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * ResEncryptTest unit test
 * @author denis.arrivault[@]univ-amu.fr
 */
import org.junit.Before;
import org.junit.Test;

import model.entity.sigma.ResEncrypt;
import util.TestInputGenerator;

public class ResEncryptTest {

	private BigInteger u;
	private BigInteger v;
	private byte[] M;
	ResEncrypt res;
	
	@Before
	public void instantiate(){
		u = TestInputGenerator.getRandomBigInteger(100);
		v = TestInputGenerator.getRandomBigInteger(100);
		M = TestInputGenerator.getRandomBytes(100);
		res = new ResEncrypt(u, v, M);
	}
	
	@Test
	public void getterSetterTest() {
		assertTrue(u.equals(res.getU()));
		assertTrue(v.equals(res.getV()));
		assertTrue(Arrays.equals(M, res.getM()));
		
		u = TestInputGenerator.getRandomBigInteger(100);
		v = TestInputGenerator.getRandomBigInteger(100);
		M = TestInputGenerator.getRandomBytes(100);
		res.setM(M);
		res.setU(u);
		res.setV(v);

		assertTrue(u.equals(res.getU()));
		assertTrue(v.equals(res.getV()));
		assertTrue(Arrays.equals(M, res.getM()));
	}
	
	@Test
	public void toStringTest(){
		String str = "<" + res.getClass().getSimpleName().toLowerCase() + ">"
				+ "<u>" + u.toString() + "</u>"
				+ "<v>" + v.toString() + "</v>"
				+ "<M>" + M.toString() + "</M>"
				+ "</" + res.getClass().getSimpleName().toLowerCase() + ">";
		assertTrue(str.equals(res.toString()));
	}
}
