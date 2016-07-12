package protocol.api;

import java.util.ArrayList;

import crypt.api.hashs.Hashable;
import crypt.api.key.AsymKey;
import crypt.api.signatures.Signable;
import crypt.api.signatures.Signer;

/**
 * Contrat interface. A contrat typically contain parties (they had to sign) and clauses
 * (purpose of the contrat)
 * The hashable data should be the concatenation of parties and clauses data.
 * @author Prudhomme Julien
 *
 * @param <T> Type of public/private key
 * @param <Key> type of actors' key
 * @param <Sign> type of actors' signature
 * @param <_Signer> signer instance
 */
public interface Contract<T, Key extends AsymKey<T>, Sign, _Signer extends Signer<Sign, Key>> extends Hashable{
	
	/**
	 * Add a party (have to sign the contrat)
	 * @param k party key, containing public key
	 */
	public void addParty(Key k);
	
	/**
	 * Get all the parties
	 * @return An array of party
	 */
	public ArrayList<Key> getParties();
	
	/**
	 * Set a signable object containing clauses
	 * @param clauses Signable clauses
	 */
	public void setClauses(Signable<Sign> clauses);
	
	/**
	 * Get the clauses
	 * @return Signable clauses
	 */
	public Signable<Sign> getClauses();
	
	/**
	 * Add a signature to the contrat
	 * @param k public key who is signing
	 * @param s the signature
	 */
	public void addSignature(Key k, Sign s);
	
	/**
	 * Get the signature according to the private key
	 * @param signer
	 * @param k the private key
	 * @return
	 */
	public Sign sign(_Signer signer, Key k);
	
	/**
	 * Check if all parties have signed the contract.
	 * @return true if all parties signed the contract
	 */
	public boolean isFinalized();
	
	/**
	 * Verify that the provided contract is equivalent to this contrat
	 * (same parties and clauses) and this contrat signatures are correct
	 * (call {@link Contract#checkSignatures(Signer)}
	 * @param contrat an other contrat to check equality
	 * @return true if contract are the sames and all parties signed
	 */
	public boolean checkContrat(Contract<T, Key, Sign, _Signer> contrat);
	
	/**
	 * Tell if 2 contract are equal: same parties and same clauses.
	 * @param c An other contract
	 * @return True if contracts are the same
	 */
	public boolean equals(Contract<T,Key,Sign,_Signer> c);
	
	/**
	 * Get the current wish of the owner of this instance
	 * @return the {@link Wish}
	 */
	public Wish getWish();
	
	/**
	 * Set the current wish of the owner of this instance
	 * @param wish
	 */
	public void setWish(Wish wish);
}
