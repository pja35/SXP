package protocol.api;

import crypt.api.signatures.Signable;

public interface Establisher {
	/**
	 * Get the contract
	 * @return c : Contract
	 */
//	public Contract<?,?,?,?> getContract();
	public String getContract();
	/**
	 * Initialize the establisher with a contract
	 * Set the signers and change status to signing
	 * @param c
	 */
//	public void initialize(Contract<?,?,?,?> c);
	public void initialize(String c, String receiverUri);
	
	public Status getStatus();
	/**
	 * Sign the contract with a Sigma-Protocol
	 */
	public void sign(String receiverUri);
	
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
