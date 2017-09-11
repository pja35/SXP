package protocol.impl.sigma;

import java.util.ArrayList;

import crypt.api.signatures.Signable;
import model.entity.sigma.SigmaSignature;

public class SigmaClauses implements Signable<SigmaSignature>{

	private SigmaSignature sign;
	private ArrayList<String> clauses;
	
	public SigmaClauses(ArrayList<String> s) {
		this.clauses = s;
	}
	
	public ArrayList<String> getClauses(){
		return clauses;
	}
	
	@Override
	public byte[] getHashableData() {
		StringBuffer buffer = new StringBuffer();
		for (String c : clauses)
			buffer.append(c);
		return buffer.toString().getBytes();
	}

	@Override
	public void setSign(SigmaSignature s) {
		this.sign = s;
	}

	@Override
	public SigmaSignature getSign() {
		return this.sign;
	}
	
	@Override
	public boolean equals(Object o) {
		SigmaClauses s2 = (SigmaClauses) o;
		if (s2.clauses.size() != this.clauses.size())
			return false;
		
		boolean b = true;
		int n = this.clauses.size();
		for (int k =0; k<n; k++)
			b = b && this.clauses.get(k).equals(s2.clauses.get(k));
		return b;
	}
}
