package protocol.impl;


import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import crypt.api.signatures.Signable;
import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import protocol.api.Contract;
import protocol.api.Establisher;
import protocol.api.Status;
import protocol.impl.sigma.Sender;
//import protocol.impl.sigma.Receiver;
//import protocol.impl.sigma.Trent;
import protocol.impl.sigma.ElGamal;
import protocol.impl.sigma.Or;
import protocol.impl.sigma.PCSFabric;
import protocol.impl.sigma.ResEncrypt;


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
	private String message;
	private Or pcs;
	private Sender sender;
//	private Receiver receiver;
//	private Trent trent;
	private ResEncrypt resEncrypt;
	private ElGamalKey receiverK;
	private ElGamalKey trentK;

	
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
	public void setResEncrypt(){
		resEncrypt = sender.Encryption(message.getBytes(), trentK);
	}
	public void setResEncrypt(int i){
		resEncrypt = sender.Encryption((i+message).getBytes(), trentK);
	}
	
	//Initialize the protocol
	/**
	 * TODO : really make the initialisation
	 * 		only implemented for tests
	 */
	public void initialize(Contract<?,?,?,?> c){
		contract = c;
	}
	public void initialize(String msg, Sender sen,ElGamalKey recK,ElGamalKey treK){
		message = msg;
		status = Status.SIGNING;
		receiverK = recK;
		trentK = treK;
		sender = sen;
		setResEncrypt();
	}
	
	//Realize the Sigma-protocol
	public void sign(){
		pcs = (new PCSFabric(sender, resEncrypt, receiverK , trentK)).getPcs();
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
