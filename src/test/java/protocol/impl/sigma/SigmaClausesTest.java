package protocol.impl.sigma;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import model.entity.sigma.SigmaSignature;

public class SigmaClausesTest {

	SigmaClauses clauses;
	SigmaClauses clauses2;
	
	@Before
	public void initialize(){
		ArrayList<String> s = new ArrayList<String>();
		s.add("Clause ");
		s.add("second Clause");
		
		clauses = new SigmaClauses(s);
		clauses2 = new SigmaClauses(s);
	}
	
	@Test
	public void getClausesTest(){
		assertTrue(clauses.getClauses().equals(clauses2.getClauses()));
	}
	
	@Test
	public void getHashableDataTest(){
		assertArrayEquals(clauses.getHashableData(), clauses2.getHashableData());
	}
	
	@Test
	public void equalTest(){
		assertTrue(clauses.equals(clauses2));
		
		ArrayList<String> s = new ArrayList<String>();
		s.add("not the same string");
		SigmaClauses clauses3 = new SigmaClauses(s);
		assertFalse(clauses.equals(clauses3));
 	}
	
	@Test
	public void getSetTest(){
		SigmaSignature sign = new SigmaSignature();
		clauses.setSign(sign);
		clauses2.setSign(sign);
		assertTrue(clauses.getSign() == clauses2.getSign());
	}
}
