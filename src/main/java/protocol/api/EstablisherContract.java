package protocol.api;


import crypt.api.hashs.Hashable;
import crypt.api.key.AsymKey;
import crypt.api.signatures.Signer;
import model.api.Status;
import model.api.Wish;
import model.entity.ContractEntity;

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
	 
	protected ContractEntity contract;
	
	
	/* 
	 * Default getters and setters
	 */
	public Wish getWish(){
		return contract.getWish();
	}
	public Status getStatus(){
		return contract.getStatus();
	}
	public void setWish(Wish w){
		contract.setWish(w);
	}
	public void setStatus(Status s){
		contract.setStatus(s);
	};
	
	
	/*
	 * Entity getter
	 */
	public ContractEntity getEntity(){
		return contract;
	}
	
	
	/**
	 * Add a signature to the contrat
	 * @param k public key who is signing
	 * @param s the signature
	 */
	public abstract void addSignature(Key k, Sign s);

	
	/**
	 * Check if all parties have signed the contract.
	 * @return true if all parties signed the contract
	 */
	public abstract boolean isFinalized();

	/**
	 * Verify that the provided contract is equivalent to this contrat
	 * (same parties and clauses) and this contrat signatures are correct
	 * (call {@link Contract#checkSignatures(Signer)}
	 * @param contrat an other contrat to check equality
	 * @return true if contract are the sames and all parties signed
	 */
	public abstract boolean checkContrat(EstablisherContract<T, Key, Sign, _Signer> contrat);

	/**
	 * Tell if 2 contract are equal: same parties and same clauses.
	 * @param c An other contract
	 * @return True if contracts are the same
	 */
	public abstract boolean equals(EstablisherContract<T,Key,Sign,_Signer> c);

	/**
	 * Returns a hash of the contract
	 */
	@Override
	public abstract byte[] getHashableData();
	
	/**
	 * Get the signature according to the private key
	 * @param signer
	 * @param k the private key
	 * @return
	 */
	public abstract Sign sign(_Signer signer, Key k);

}
