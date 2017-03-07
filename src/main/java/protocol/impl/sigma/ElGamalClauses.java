package protocol.impl.sigma;

import java.util.ArrayList;

import crypt.api.signatures.Signable;
import crypt.impl.signatures.ElGamalSignature;

public class ElGamalClauses implements Signable<ElGamalSignature> {

	private ElGamalSignature sign;
	private ArrayList<String> clauses;
	
	public ElGamalClauses(ArrayList<String> s) {
		this.clauses = s;
	}
	
	public ArrayList<String> getClauses(){
		return clauses;
	}
	
	@Override
	public byte[] getHashableData() {
		String res = "";
		for (String c : clauses)
			res = res.concat(c);
		return res.getBytes();
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
		if (s2.clauses.size() != this.clauses.size())
			return false;
		
		boolean b = true;
		int n = this.clauses.size();
		for (int k =0; k<n; k++)
			b = b && this.clauses.get(k).equals(s2.clauses.get(k));
		return b;
	}
}
