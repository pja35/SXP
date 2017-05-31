package protocol.impl;


import java.math.BigInteger;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;

import crypt.impl.signatures.SigmaSigner;

import controller.Application;
import controller.tools.JsonTools;
import model.api.Status;
import model.api.Wish;
import model.entity.ElGamalKey;
import model.entity.sigma.SigmaSignature;
import network.api.EstablisherService;
import network.api.Peer;
import protocol.api.Establisher;
import protocol.impl.sigma.SigmaContract;
import protocol.impl.sigma.SigmaEstablisherData;
import protocol.impl.sigma.steps.ProtocolChooseTrent;
import protocol.impl.sigma.steps.ProtocolResolve;
import protocol.impl.sigma.steps.ProtocolSign;
import protocol.impl.sigma.steps.ProtocolStep;


/** 
 *	Establisher for sigma protocol
 *
 * @author Nathanaël EON
 */

public class SigmaEstablisher extends Establisher<BigInteger, ElGamalKey, SigmaSignature, SigmaSigner, SigmaContract> {
	
	// The service we'll use to send data
	public EstablisherService establisherService =(EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);
	// The current peer
	public Peer peer = Application.getInstance().getPeer();
	// The step called if something goes wrong
	public ProtocolStep resolvingStep;
	// Data that need to be restored if there is a peer-disconnection
	// TODO implement correctly the data saving
	public SigmaEstablisherData sigmaEstablisherData;
	
	// The signer Key
	protected ElGamalKey senderK;
	
	/**
	 * Setup the signing protocol
	 * @param <senderK> : elgamalkey (public and private) of the user
	 * @param <uri> : parties matching uri, if null, the protocol will be asynchronous
	 */
	public SigmaEstablisher(ElGamalKey senderK, HashMap<ElGamalKey,String> uris){
		this.senderK = senderK;
		
		this.uris = uris;
			
		this.sigmaEstablisherData = new SigmaEstablisherData();
		this.sigmaEstablisherData.setUris(uris);
		sigmaEstablisherData.setTrentKey(null);
	}
	
	/**
	 * Constructor which start back from where it were (using establisherData)
	 * @param <establisherData> : data of the former establisher 
	 * @param <senderK> : key we use to sign contract
	 */
	public SigmaEstablisher(String establisherData, ElGamalKey senderK){
		JsonTools<SigmaEstablisherData> json = new JsonTools<>(new TypeReference<SigmaEstablisherData>(){});
		SigmaEstablisherData data = json.toEntity(establisherData);
		this.contract = data.getContract();
		this.uris = data.getUris();
		this.sigmaEstablisherData = data;
	}
	
	/**
	 * Initialize the protocol when a contract comes
	 * @param <c> : contract to be signed
	 */
	@Override
	public void initialize(SigmaContract c){
		this.sigmaEstablisherData.setContract(c);
		contract = c;
		
		//Prepare the choosingTent step
		sigmaEstablisherData.setProtocolStep( new ProtocolChooseTrent(this, senderK) );
	}
	
	/**
	 * Launch the protocol : tell everyone that the user is ready to sign (pressed signing button)
	 */
	@Override
	public void start(){
		// Does only start if the status and the wish are ok
		// It sends the list of users that can be TTP for us
		if (getStatus() != Status.CANCELLED && getStatus() != Status.FINALIZED && getWish() == Wish.ACCEPT)
			sigmaEstablisherData.getProtocolStep().sendMessage();
	}
	
	/**
	 * Setup trent with correct Key
	 * @param trentK
	 */
	public void setTrent(ElGamalKey trentK){
		contract.setTrentKey(trentK);
		sigmaEstablisherData.setTrentKey(trentK);
	}

	
	// Put a listener on Trent in case something goes wrong
	public void setListenerOnTrent(){
		setStatus(Status.SIGNING);
		resolvingStep = new ProtocolResolve(this,
				senderK);
		sign();
	}
	
	/**
	 * The contract signing protocol
	 * TODO : Setup a timer that will trigger resolve()
	 */
	protected void sign(){
		sigmaEstablisherData.setProtocolStep(new ProtocolSign(this, senderK));
		sigmaEstablisherData.getProtocolStep().sendMessage();
	}
	
	/**
	 * Called if something goes wrong.
	 * It send Trent 5 informations : 
	 *		the round
	 * 		the uris of the parties
	 * 		the contract to be signed
	 * 		the encrypted (for Trent) claim
	 * 		the encrypted (for Trent) signed claim (we need to check signature)
	 * 
	 * Trent resolve function is in Trent Class
	 */
	protected void resolve(){
		resolvingStep.sendMessage();
	}
}
