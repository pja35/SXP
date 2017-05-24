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
		String res = "";
		for (String c : clauses)
			res = res.concat(c);
		return res.getBytes();
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
