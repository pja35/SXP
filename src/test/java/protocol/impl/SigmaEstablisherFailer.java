package protocol.impl;

import java.util.HashMap;

import model.api.Status;
import model.entity.ElGamalKey;
import network.api.Messages;
import network.api.ServiceListener;
import protocol.impl.sigma.PCS;
import protocol.impl.sigma.Sender;

public class SigmaEstablisherFailer extends SigmaEstablisher{

	private int failingRound;
	
	public SigmaEstablisherFailer(String token, HashMap<ElGamalKey, String> uri, ElGamalKey t, int f) {
		super(token, uri, t);
		this.failingRound = f;
	}
	
	public SigmaEstablisherFailer(ElGamalKey k, HashMap<ElGamalKey, String> uri, ElGamalKey t, int f) {
		super(k, uri, t);
		this.failingRound = f;
	}
	
	@Override
	protected void sign(){
		setStatus(Status.SIGNING);
		
		// Necessary tools to create the PCS
		Sender sender = new Sender(signer.getKey());
		pcs = new PCS(sender, trentK);
		round = 1;

		establisherService.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				if (messages.getMessage("title").equals(contractId)){
					String msg = messages.getMessage("contract");
					
					// Checks if the message is a PCS, if yes store it in "pcs[round][k]"
					verifyAndStoreSignature(msg, messages.getMessage("sourceId"));
					

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
					if (round >= failingRound){
						resolve();
					} else{
						while (round<=(N+1) && claimFormed){
							sendRound(++round);
							for (int k=0; k<N; k++){
								if (round >= failingRound){
									claimFormed = false;
									resolve();
								}else if (promRoundSender[round][k] == null )
									claimFormed= false;
							}
						}
					}
				}
			}
		}, senPubK.toString());

		// Send the first round
		sendRound(1);
	}
}
