package protocol.impl.contract;

import crypt.api.signatures.Signable;
import crypt.impl.signatures.ElGamalSignature;

public class ElGamalClauses implements Signable<ElGamalSignature> {

	private ElGamalSignature sign;
	private String clauses;
	
	public ElGamalClauses(String s) {
		this.clauses = s;
	}
	
	@Override
	public byte[] getHashableData() {
		return clauses.getBytes();
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
		ElGamalClauses s2 = (ElGamalClauses) o;
		return this.clauses.equals(s2.clauses);
	}
}
