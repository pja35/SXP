package model.entity;


import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
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
 * ElGamalSignEntity unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
@RunWith(value = Parameterized.class)
public class ElGamalSignEntityTest {
	@Rule public ExpectedException exception = ExpectedException.none();
	ElGamalSignEntity sign = new ElGamalSignEntity();
	BigInteger r, s;
	
	
	
	public ElGamalSignEntityTest(BigInteger r, BigInteger s) {
		this.r = r;
		this.s = s;
	}

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			{TestInputGenerator.getRandomBigInteger(200), TestInputGenerator.getRandomBigInteger(200)}, 
			{TestInputGenerator.getRandomBigInteger(10), TestInputGenerator.getRandomBigInteger(10)} 
		});
	}

	@Test
	public void getAnnotatedParamTest() {
		sign.setR(r);
		assertTrue(sign.getR().equals(r));		
		sign.setS(s);
		assertTrue(sign.getS().equals(s));	
	}
	
	//@Test
	//This test could not work as paramName has no runtime retention instruction
	public void setGetParamTest() {
		sign.setR(r);
		assertTrue(sign.getR() == sign.getParam("r"));		
		sign.setS(s);
		assertTrue(sign.getS() == sign.getParam("s"));	
	}
	
	@Test
	public void badParamTest(){
		String param = TestInputGenerator.getRandomIpsumText(1);
		exception.expect(RuntimeException.class);
		exception.expectMessage("Undefined param : " + param);
		sign.getParam(param);
	}
}
