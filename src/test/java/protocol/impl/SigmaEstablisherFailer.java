package protocol.impl;


import java.util.HashMap;

import model.api.Status;
import model.entity.ElGamalKey;
import network.api.EstablisherServiceListener;
import protocol.impl.sigma.PCS;
import protocol.impl.sigma.Sender;

/**
 * 
 * @author Nathanaël Eon
 *
 * Hack of SigmaEstablisher to produce fails
 */
public class SigmaEstablisherFailer extends SigmaEstablisher{

	private int failingRound;
	private boolean liar;
	
	public SigmaEstablisherFailer(ElGamalKey k, ElGamalKey t, HashMap<ElGamalKey, String> uris, int f, boolean liar) {
		super(k, t, uris);
		this.failingRound = f;
		this.liar = liar;
	}
	
	@Override
	protected void sign(){
		setStatus(Status.SIGNING);
		
		// Necessary tools to create the PCS
		Sender sender = new Sender(signer.getKey());
		pcs = new PCS(sender, trentK);
		round = 1;

		establisherService.removeListener(SIGNING_MESSAGE+contractId+senPubK);
		establisherService.setListener("title", SIGNING_MESSAGE+contractId, SIGNING_MESSAGE+contractId+senPubK, new EstablisherServiceListener() {
			@Override
			public void notify(String title, String msg, String senderId) {
				// Checks if the message is a PCS, if yes store it in "pcs[round][k]"
				verifyAndStoreSignature(msg, senderId);
				

				// Check if the round is complete
				boolean claimFormed = true;
				for (int k=0; k<N; k++){
					if (promRoundSender[round][k] == null){
						claimFormed= false;
					}
				}
				/*	Send the rounds (if we have the claim needed):
				 *  	We do a loop because sometimes, we receive the PCS for round+1 before the one for the current round
				 */  
				if (round == failingRound){
					resolve();
				}
				if (round< failingRound || liar){
					while (round<=(N+1) && claimFormed){
						sendRound(++round);
						if (round == failingRound){
							resolve();
							if (!liar)
								claimFormed = false;
						}else{
							for (int k=0; k<N; k++){
								if (promRoundSender[round][k] == null )
									claimFormed= false;
							}
						}
					}
				}
			}
		}, uris != null);

		// Send the first round
		sendRound(1);
	}
}
