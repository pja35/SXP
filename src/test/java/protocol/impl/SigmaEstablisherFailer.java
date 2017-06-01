package protocol.impl;


import java.util.HashMap;

import model.api.Status;
import model.entity.ElGamalKey;
import protocol.impl.sigma.step.ProtocolResolveFailer;
import protocol.impl.sigma.step.ProtocolSignFailer;
import protocol.impl.sigma.step.ProtocolSignFailerLiar;
import protocol.impl.sigma.steps.ProtocolResolve;

/**
 * 
 * @author Nathanaël Eon
 *
 * Hack of SigmaEstablisher to produce fails
 */
public class SigmaEstablisherFailer extends SigmaEstablisher{

	public int failingRound;
	public int failingRound2 = 0;
	private boolean liar;
	
	public SigmaEstablisherFailer(ElGamalKey k, HashMap<ElGamalKey, String> uris, int f, boolean l) {
		super(k, uris);
		this.failingRound = f;
		this.liar = l;
	}
	
	public SigmaEstablisherFailer(ElGamalKey k, HashMap<ElGamalKey, String> uris, int f, int f2) {
		super(k, uris);
		this.failingRound = f;
		this.failingRound2 = f2;
		this.liar = true;
	}
	
	@Override
	protected void sign(){
		if (this.liar)
			sigmaEstablisherData.setProtocolStep(new ProtocolSignFailerLiar(this, senderK, this.establisherService, peer, uris, this.contract));
		else
			sigmaEstablisherData.setProtocolStep(new ProtocolSignFailer(this, senderK, this.establisherService, peer, uris, this.contract));
		sigmaEstablisherData.getProtocolStep().sendMessage();
	}

	// Put a listener on Trent in case something goes wrong
	@Override
	public void setListenerOnTrent(){
		setStatus(Status.SIGNING);

		if (this.liar)
			resolvingStep = new ProtocolResolveFailer(this, senderK);
		else
			resolvingStep = new ProtocolResolve(this,senderK);
		
		sign();
	}
}
