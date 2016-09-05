package protocol.impl.contract;

import java.math.BigInteger;

import crypt.api.signatures.Signable;
import crypt.impl.signatures.ElGamalSignature;
import crypt.impl.signatures.ElGamalSigner;
import model.entity.ElGamalKey;
import protocol.base.BaseContract;

/**
 * El-gamal based contract
 * @author Prudhomme Julien
 *
 */
public class ElGamalContract extends BaseContract<BigInteger, ElGamalKey, ElGamalSignature, ElGamalSigner>{
	
	
	/**
	 * Create a new contract with an ElGamalSigner
	 */
	public ElGamalContract() {
		super();
		this.signer = new ElGamalSigner();
	}
	
	public ElGamalContract(Signable<ElGamalSignature> clauses) {
		super(clauses);
		this.signer = new ElGamalSigner();
	}
}
