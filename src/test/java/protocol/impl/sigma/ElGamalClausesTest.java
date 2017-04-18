package protocol.impl.sigma;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import crypt.impl.signatures.ElGamalSignature;
import util.TestInputGenerator;

/**
 * ElGamalClauses unit test
 * @author nathanael.eon[@]lif.univ-mrs.fr
 *
 */
public class ElGamalClausesTest {
	private ArrayList<String> clauses;
	private ElGamalClauses egClauses;
	private ElGamalSignature sign;
	
	@Before
	public void instantiate(){
		clauses = new ArrayList<String>();
		clauses.add("First clause");
		clauses.add("Second clause");
		sign = new ElGamalSignature(TestInputGenerator.getRandomBigInteger(100),TestInputGenerator.getRandomBigInteger(100));
		egClauses = new ElGamalClauses(clauses);
	}
	
	@Test
	public void getClauses(){
		assertTrue(egClauses.getClauses().toString().equals(clauses.toString()));
	}
	
	@Test
	public void getHashableDataTest(){
		String res = "";
		for (String c : clauses)
			res = res.concat(c);
		assertArrayEquals(egClauses.getHashableData(), res.getBytes());
	}
	
	@Test
	public void setGetSignTest(){
		egClauses.setSign(sign);
		assertTrue(egClauses.getSign().getR().equals(sign.getR()));
		assertTrue(egClauses.getSign().getS().equals(sign.getS()));
	}
	
	@Test
	public void equalsTest(){
		ElGamalClauses cl2 = new ElGamalClauses(new ArrayList<String>());
		assertFalse(cl2.equals(egClauses));
		ElGamalClauses cl3 = new ElGamalClauses(clauses);
		assertTrue(cl3.equals(egClauses));
	}
}
