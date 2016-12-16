package protocol.impl;

import crypt.api.signatures.Signable;
import protocol.api.Contract;
import protocol.api.Establisher;
import protocol.api.Status;
import protocol.impl.sigma.Sender;
import protocol.impl.sigma.Receiver;
import protocol.impl.sigma.Trent;

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
	private Sender sender;
	private Receiver receiver;
	private Trent trent;

	
	//Getters
	public Contract<?,?,?,?> getContract(){
		return contract;
	}
	public Status getStatus(){
		return status;
	}
	
	//Initialize the protocol
	public void initialize(Contract<?,?,?,?> c){
		status = Status.SIGNING;
		contract = c;
	}
	
	//Realize the Sigma-protocol
	public void sign(){
		
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
}
