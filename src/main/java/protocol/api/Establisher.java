package protocol.api;

import crypt.api.signatures.Signable;

public interface Establisher {
	/**
	 * Get the contract
	 * @return c : Contract
	 */
	public Contract<?,?,?,?> getContract();
	/**
	 * Initialize the establisher with a contract
	 * Set the signers and change status to signing
	 * @param c
	 */
	public Status getStatus();
	
	public void initialize(Contract<?,?,?,?> c);
	
	/**
	 * Sign the contract with a Sigma-Protocol
	 */
	public void sign();
	
	/**
	 * Resolve function when something goes wrong with the protocol
	 * @param k : round of the protocol
	 */
	public Signable<?> resolve(int k);
	
	/**
	 * Trent (TTP) resolve function
	 */
	public Signable<?> resolveTrent();
	
}
