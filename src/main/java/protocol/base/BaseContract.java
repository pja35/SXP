package protocol.base;

import java.util.ArrayList;
import java.util.HashMap;

import org.bouncycastle.util.Arrays;

import crypt.api.key.AsymKey;
import crypt.api.signatures.Signable;
import crypt.api.signatures.Signer;
import protocol.api.Contract;
import protocol.api.Wish;

public class BaseContract<T, Key extends AsymKey<T>, Sign, _Signer extends Signer<Sign, Key>> implements Contract<T, Key, Sign, _Signer>{

	protected ArrayList<Key> parties = new ArrayList<>();
	protected HashMap<Key, Sign> signatures = new HashMap<Key, Sign>();
	protected Signable<Sign> clauses = null;
	protected _Signer signer;
	protected Wish wish = Wish.NEUTRAL;
	
	/**
	 * Create an empty contrat
	 */
	public BaseContract() {}
	
	/**
	 * Create a new contract with clauses
	 * @param clauses the clauses
	 */
	public BaseContract(Signable<Sign> clauses) {
		setClauses(clauses);
	}
	
	@Override
	public void addParty(Key k) {
		parties.add(k);
	}

	@Override
	public ArrayList<Key> getParties() {
		return parties;
	}

	@Override
	public void setClauses(Signable<Sign> clauses) {
		this.clauses = clauses;
	}
	
	@Override
	public Signable<Sign> getClauses() {
		return this.clauses;
	}
	
	@Override
	public void addSignature(Key k, Sign s) {
		if(k == null || !this.parties.contains(k)) {
			throw new RuntimeException("invalid key");
		}
		signatures.put(k, s);
	}

	@Override
	public boolean isFinalized() {
		for(Key k: parties) {
			signer.setKey(k);
			if(signatures.get(k) == null || !signer.verify(clauses.getHashableData(), signatures.get(k))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean checkContrat(Contract<T, Key, Sign, _Signer> contrat) {
		return this.equals(contrat) && this.isFinalized();
	}
	
	public boolean equals(Contract<T,Key,Sign,_Signer> c) {
		return Arrays.areEqual(this.getHashableData(), c.getHashableData());
	}

	@Override
	public byte[] getHashableData() {
		StringBuffer buffer = new StringBuffer();
		for(Key k: getParties()) {
			buffer.append(k.getPublicKey().toString());
		}
		byte[] signable = this.getClauses().getHashableData();
		
		int signableL = signable.length;
		int bufferL = buffer.toString().getBytes().length;
		byte[] concate = new byte[signableL + bufferL];
		System.arraycopy(buffer.toString().getBytes(), 0, concate, 0, bufferL);
		System.arraycopy(signable, 0, concate, bufferL, signableL);
		
		return concate;
	}

	@Override
	public Sign sign(_Signer signer, Key k) {
		return signer.sign(getClauses());
	}

	@Override
	public Wish getWish() {
		return wish;
	}

	@Override
	public void setWish(Wish wish) {
		this.wish = wish;
	}

}
