package crypt.impl;
import static org.junit.Assert.*;

import org.junit.Test;

import crypt.api.signatures.Signable;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.impl.key.ElGamalAsymKey;
import crypt.impl.signatures.ElGamalSignature;
import crypt.impl.signatures.ElGamalSigner;
import model.entity.ElGamalKey;
import protocol.impl.contract.ElGamalContract;

public class ElGamalContractTest {
	private static final int N = 5;
	private static final String sign1 = "Lorem ipsum";
	private static final String sign2 = "Ipsum lorem";
	
	class ExempleSignable implements Signable<ElGamalSignature> {
		private ElGamalSignature sign;
		private String exemple;
		
		public ExempleSignable(String s) {
			this.exemple = s;
		}
		
		@Override
		public byte[] getHashableData() {
			return exemple.getBytes();
		}

		@Override
		public void setSign(ElGamalSignature s) {
			this.sign = s;
		}

		@Override
		public ElGamalSignature getSign() {
			return this.sign;
		}
		
		public boolean equals(Object o) {
			ExempleSignable s2 = (ExempleSignable) o;
			return this.exemple.equals(s2.exemple);
		}
		
	}
	
	@Test
	public void test() {
		ExempleSignable signable1 = new ExempleSignable(sign1);
		ExempleSignable signable2 = new ExempleSignable(sign1);
		ElGamalKey[] keys = new ElGamalKey[N];
		
		for(int i = 0; i < N; i++) {
			keys[i] = ElGamalAsymKeyFactory.create(false);
		}
		ElGamalContract c1 = new ElGamalContract();
		ElGamalContract c2 = new ElGamalContract();
		c1.setClauses(signable1);
		c2.setClauses(signable1);
		ElGamalSigner signer = new ElGamalSigner();
		
		for(int i = 0; i < N-1; i++) {
			signer.setKey(keys[i]);
			c1.addParty(keys[i]);
			c2.addParty(keys[i]);
			c1.addSignature(keys[i], c1.sign(signer, keys[i]));
			c2.addSignature(keys[i], c2.sign(signer, keys[i]));
		}
		
		assertTrue(signable1.equals(signable2));
		assertEquals(c1, c1);
		
		assertTrue(c1.equals(c1));
		
		signable2 = new ExempleSignable(sign2);
		assertTrue(c1.equals(c2));
		assertTrue(c1.checkContrat(c2));
		c2.setClauses(signable2);
		assertFalse(c1.checkContrat(c2));
		
		c1.addParty(keys[N-1]);
		assertFalse(c1.isFinalized());
		
		signer.setKey(keys[N-1]);
		c1.addSignature(keys[N-1], c1.sign(signer, keys[N-1]));
		assertTrue(c1.isFinalized());
	}
}
