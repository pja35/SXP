package protocol.impl;


import java.math.BigInteger;
import java.util.HashMap;


import crypt.impl.signatures.SigmaSigner;

import controller.Application;
import crypt.factories.SignerFactory;
import model.api.Status;
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
import protocol.impl.sigma.steps.ProtocolStart;
import protocol.impl.sigma.steps.ProtocolStep;


/** 
 *	Establisher for sigma protocol
 *
 * @author Nathanaël EON
 *
 * TODO : Change the messaging system to an asymetric one
 */

public class SigmaEstablisher extends Establisher<BigInteger, ElGamalKey, SigmaSignature, SigmaSigner, SigmaContract> {
	
	public static final String TRENT_CHOOSING_MESSAGE = "CHOOSE_TRENT";
	
	public SigmaEstablisherData sigmaEstablisherData;
	
	protected EstablisherService establisherService =(EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);
	protected Peer peer = Application.getInstance().getPeer();
	public ProtocolStep resolvingStep;
	
	/**
	 * Setup the signing protocol
	 * @param <senderK> : elgamalkey (public and private) of the user
	 * @param <uri> : parties matching uri
	 */
	// TODO : REMOVE trentK
	public SigmaEstablisher(ElGamalKey senderK, HashMap<ElGamalKey,String> uris){
		this.signer = SignerFactory.createSigmaSigner();
		this.signer.setKey(senderK);
		
		this.uris = uris;
		
		this.sigmaEstablisherData = new SigmaEstablisherData();
		sigmaEstablisherData.setSenderKey(senderK);
	}
	
	/**
	 * @param <c> : contract to be signed
	 */
	@Override
	public void initialize(SigmaContract c){
		contract = c;
		/*
		 * Get ready to start
		 * If an advertisement was or is received, we check the signature and it is stored and we wait until everyone has sent its starter
		 */
		sigmaEstablisherData.setProtocolStep(new ProtocolStart(this, signer.getKey(), peer, uris, establisherService, contract));
		sigmaEstablisherData.getProtocolStep().setupListener();
	}
	
	/**
	 * Launch the protocol : tell everyone that the user is ready to sign (pressed signing button)
	 */
	@Override
	public void start(){
		sigmaEstablisherData.getProtocolStep().sendMessage();
	}

	/**
	 * Choose Trent, put a listener for him, then start signing
	 */
	public void chooseTrent(){
		sigmaEstablisherData.getProtocolStep().stop();
		
		sigmaEstablisherData.setProtocolStep(
			new ProtocolChooseTrent(this,
				peer,
				uris,
				establisherService,
				contract));
		sigmaEstablisherData.getProtocolStep().setupListener();
		sigmaEstablisherData.getProtocolStep().sendMessage();
	}
	public void setTrent(ElGamalKey trentK){
		contract.setTrentKey(trentK);
		signer.setTrentK(trentK);
		sigmaEstablisherData.setTrentKey(trentK);
	}

	
	// Put a listener on Trent in case something goes wrong
	public void setListenerOnTrent(){
		resolvingStep = new ProtocolResolve(this,
				establisherService,
				peer,
				contract,
				signer);
		resolvingStep.setupListener();
		
		if (getStatus() != Status.CANCELLED && getStatus() != Status.FINALIZED)
			sign();
	}
	
	/**
	 * The contract signing protocol
	 * TODO : Setup a timer that will trigger resolve()
	 */
	protected void sign(){
		sigmaEstablisherData.setProtocolStep(new ProtocolSign(this, this.establisherService, peer, uris, this.contract));
		sigmaEstablisherData.getProtocolStep().setupListener();
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
