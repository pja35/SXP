package protocol.impl;


import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import crypt.api.signatures.Signable;
import model.entity.ElGamalKey;
import protocol.api.Contract;
import protocol.api.Establisher;
import protocol.api.Status;
import protocol.impl.sigma.Sender;
//import protocol.impl.sigma.Trent;
import protocol.impl.sigma.ElGamal;
import protocol.impl.sigma.Or;
import protocol.impl.sigma.PCSFabric;


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
	 * Sender, Receiver, Trent : instances necessary to the signature
	 */
	private Status status = Status.NOWHERE;
	private Contract<?,?,?,?> contract;
	private byte[] message;
	private Sender sender;
//	private Trent trent;
	private ElGamalKey receiverK;
	private ElGamalKey trentK;
	private Or pcs;

	
	//Getters
	public Contract<?,?,?,?> getContract(){
		return contract;
	}
	public Status getStatus(){
		return status;
	}
	public Or getPrivateCS(){
		return pcs;
	}

	//Setters
	public void initialize(Contract<?,?,?,?> c){
		message = c.toString().getBytes();
		contract = c;
	}
	public void initialize(String msg){
		message = msg.getBytes();
	}
	
	
	/**
	 * Constructor
	 * @param sen : Sender
	 * @param receK : Receiver Keys
	 * @param trenK : Trent Keys
	 */
	public SigmaEstablisher(Sender sen, ElGamalKey receK, ElGamalKey trenK){
		receiverK = receK;
		trentK = trenK;
		sender = sen;
	}
	
	//Realize the Sigma-protocol (called when the user click on "Sign")
	public void sign(){
		status = Status.SIGNING;
		PCSFabric pcsf = new PCSFabric(sender, receiverK , trentK); 
		pcs = pcsf.getPcs(message);
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
	
	
	/**
	 * What follows is the necessary primitives for the signature to be done over the network
	 * @return
	 */
	
	//Return the string representing the private contract signature
	public String getJson(){
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		return json.toJson(pcs, true);
	}
	//Return the encrypted string representing the private contract signature using receiver key
	public String getJson(boolean toEncrypt){
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		String msg = json.toJson(pcs, true);
		ElGamal eg = new ElGamal(receiverK);
		return new String(eg.encryptWithPublicKey(msg.getBytes()));
	}

	//Return the PCS (Or Object) from json
	public Or getPrivateCS(String pcs){
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		return json.toEntity(pcs, true);
	}
	//Return the PCS (Or Object) from json when it is encrypted with sender publicKey
	public Or getPrivateCS(String pcs, boolean encrypted){
		ElGamal eg = new ElGamal(sender.getKeys());
		byte[] msg = eg.decryptWithPrivateKey(pcs.getBytes());
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		return json.toEntity(new String(msg), true);
	}
}
