package protocol.impl;


import java.util.HashMap;

import model.entity.ElGamalKey;
import protocol.impl.sigma.ProtocolSignFailer;

/**
 * 
 * @author Nathanaël Eon
 *
 * Hack of SigmaEstablisher to produce fails
 */
public class SigmaEstablisherFailer extends SigmaEstablisher{

	public int failingRound;
	
	public SigmaEstablisherFailer(ElGamalKey k, ElGamalKey t, HashMap<ElGamalKey, String> uris, int f, boolean liar) {
		super(k, t, uris);
		this.failingRound = f;
	}
	
	@Override
	protected void sign(){
		sigmaEstablisherData.setProtocolStep(new ProtocolSignFailer(this, this.establisherService, peer, uris, this.contract));
		sigmaEstablisherData.getProtocolStep().setupListener();
		sigmaEstablisherData.getProtocolStep().sendMessage();
	}
}
