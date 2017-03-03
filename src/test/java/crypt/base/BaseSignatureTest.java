package crypt.base;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import util.TestInputGenerator;

/**
 * BaseSignature unit test
 * @author denis.arrivault[@]univ-amu.fr
 */
public class BaseSignatureTest {
	@Rule public ExpectedException exception = ExpectedException.none();
	
	BaseSignature<String> sign;
	
	public BaseSignatureTest() {
		this.sign = new BaseSignature<String>();
	}

	/**
	 * Test method for {@link crypt.base.BaseSignature#getParam(java.lang.String)}.
	 */
	@Test
	public void testGetParam() {
		String param = TestInputGenerator.getRandomIpsumText(1);
		exception.expect(RuntimeException.class);
		exception.expectMessage("Undefined param : " + param);
		sign.getParam(param);
	}

}

