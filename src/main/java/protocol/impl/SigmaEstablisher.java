package protocol.impl;

import java.math.BigInteger;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import crypt.api.signatures.Signable;
import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import protocol.api.Contract;
import protocol.api.Establisher;
import protocol.api.Status;
import protocol.impl.sigma.Sender;
import protocol.impl.sigma.Receiver;
import protocol.impl.sigma.Trent;
import protocol.impl.sigma.And;
import protocol.impl.sigma.Masks;
import protocol.impl.sigma.PrivateContractSignature;
import protocol.impl.sigma.ResEncrypt;
import protocol.impl.sigma.Responses;


/**
 * 
 * @author Nathanaël EON
 *
 *	Implements the sigma protocol
 */

public class SigmaEstablisher implements Establisher{
	
	/**
	 * status : ongoing state of signature
	 * contract : contract which is being signed
	 * Sender, Receiver, Trent : instances necesseray to the signature
	 */
	private Status status = Status.NOWHERE;
	private Contract<?,?,?,?> contract;
	private PrivateContractSignature pcs;
	private Sender sender;
	private Receiver receiver;
	private Trent trent;
	private ResEncrypt resEncrypt;
	private ElGamalKey aliceK;
	private ElGamalKey trentK;

	
	//Getters
	public Contract<?,?,?,?> getContract(){
		return contract;
	}
	public Status getStatus(){
		return status;
	}
	
	//Initialize the protocol
	/**
	 * TODO : really make the initialisation
	 * 		only implemented for tests
	 */
	public void initialize(Contract<?,?,?,?> c){
		contract =c;
	}
	public void initialize(String s){
		status = Status.SIGNING;
		ElGamalKey bobK;
		bobK = ElGamalAsymKeyFactory.create(false);
		aliceK = ElGamalAsymKeyFactory.create(false);
		trentK = ElGamalAsymKeyFactory.create(false);
		sender = new Sender(bobK);
		resEncrypt = sender.Encryption(s.getBytes(), trentK);
	}
	
	//Realize the Sigma-protocol
	public void sign(){
		pcs = new PrivateContractSignature(sender, resEncrypt, aliceK , trentK);
	}

	private Signable<?> s;
	//Resolve in case of error
	public Signable<?> resolve(int k){
		return s;
	}
	
	//What Trent got to do
	public Signable<?> resolveTrent(){
		return s;
	}
	
	public String getPcs(){
		JsonTools<PrivateContractSignature> json = new JsonTools<>(new TypeReference<PrivateContractSignature>(){});
		return json.toJson(pcs);
	}
	
	public PrivateContractSignature getPrivateCS(String pcs){
		JsonTools<PrivateContractSignature> json = new JsonTools<>(new TypeReference<PrivateContractSignature>(){});
		return json.toEntity(pcs);
	}
	
	public PrivateContractSignature getPrivateCS(){
		return pcs;
	}
}
