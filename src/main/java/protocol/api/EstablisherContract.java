package protocol.api;

import java.util.ArrayList;
import java.util.HashMap;

import org.bouncycastle.util.Arrays;

import crypt.api.hashs.Hashable;
import crypt.api.key.AsymKey;
import crypt.api.signatures.Signable;
import crypt.api.signatures.Signer;

/**
 * Contrat abstract class. A contrat typically contain parties (they had to sign) and clauses
 * (purpose of the contrat). This contract will be used by establishers. An example of 
 * contract : impl.sigma.SigmaContractAdapter
 * The hashable data should be the concatenation of parties and clauses data.
 * @author Nathanaël Eon
 *
 * @param <T> Type of public/private key
 * @param <Key> type of actors' key
 * @param <Sign> type of actors' signature
 * @param <_Signer> signer instance
 */
public abstract class EstablisherContract<T, Key extends AsymKey<T>, Sign, _Signer extends Signer<Sign, Key>> implements Hashable{
	
	protected String title = "";
	protected ArrayList<Key> parties = new ArrayList<>();
	protected HashMap<Key,String> partiesId = new HashMap<Key, String>(); // Match the key and the id of users
	protected HashMap<Key, Sign> signatures = new HashMap<Key, Sign>();
	protected Signable<Sign> clauses = null;
	protected _Signer signer;
	
	/**
	 * Create an empty contrat
	 */
	public EstablisherContract() {}
	
	/**
	 * Create a new contract with clauses
	 * @param clauses the clauses
	 */
	public EstablisherContract(Signable<Sign> clauses) {
		setClauses(clauses);
	}

	/**
	 * Add a party (have to sign the contrat)
	 * @param k party key, containing public key
	 */
	public void addParty(Key k) {
		parties.add(k);
	}

	/**
	 * Get all the parties
	 * @return An array of party
	 */
	public ArrayList<Key> getParties() {
		return parties;
	}

	/**
	 * Set a signable object containing clauses
	 * @param clauses Signable clauses
	 */
	public void setClauses(Signable<Sign> clauses) {
		this.clauses = clauses;
	}

	/**
	 * Get the clauses
	 * @return Signable clauses
	 */
	public Signable<Sign> getClauses() {
		return this.clauses;
	}
	

	public void setTitle(String t){
		this.title=t;
	}
	
	public String getTitle(){
		return title;
	}

	/**
	 * Add a signature to the contrat
	 * @param k public key who is signing
	 * @param s the signature
	 */
	public void addSignature(Key k, Sign s) {
		if(k == null || !this.parties.contains(k)) {
			throw new RuntimeException("invalid key");
		}
		signatures.put(k, s);
	}

	
	/**
	 * Check if all parties have signed the contract.
	 * @return true if all parties signed the contract
	 */
	public boolean isFinalized() {
		for(Key k: parties) {
			signer.setKey(k);
			if(signatures.get(k) == null || !signer.verify(clauses.getHashableData(), signatures.get(k))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Verify that the provided contract is equivalent to this contrat
	 * (same parties and clauses) and this contrat signatures are correct
	 * (call {@link Contract#checkSignatures(Signer)}
	 * @param contrat an other contrat to check equality
	 * @return true if contract are the sames and all parties signed
	 */
	public boolean checkContrat(EstablisherContract<T, Key, Sign, _Signer> contrat) {
		return this.equals(contrat) && this.isFinalized();
	}

	/**
	 * Tell if 2 contract are equal: same parties and same clauses.
	 * @param c An other contract
	 * @return True if contracts are the same
	 */
	public boolean equals(EstablisherContract<T,Key,Sign,_Signer> c) {
		return Arrays.areEqual(this.getHashableData(), c.getHashableData());
	}

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

	/**
	 * Get the signature according to the private key
	 * @param signer
	 * @param k the private key
	 * @return
	 */
	public Sign sign(_Signer signer, Key k) {
		return signer.sign(getClauses());
	}

}
