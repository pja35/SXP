package protocol.impl.sigma.step;

import java.util.HashMap;

import model.entity.ElGamalKey;
import network.api.EstablisherService;
import network.api.EstablisherServiceListener;
import network.api.Peer;
import protocol.impl.SigmaEstablisherFailer;
import protocol.impl.sigma.SigmaContract;
import protocol.impl.sigma.steps.ProtocolSign;

public class ProtocolSignFailer extends ProtocolSign{

	private boolean resolvedSent = false;
	int failingRound;
	
	public ProtocolSignFailer(SigmaEstablisherFailer sigmaE, ElGamalKey key, EstablisherService es, Peer peer,
			HashMap<ElGamalKey, String> uris, SigmaContract contract) {
		super(sigmaE, key);
		failingRound = ((SigmaEstablisherFailer) sigmaEstablisher).failingRound;
	}

	@Override
	public void setupListener() {
		String contractId = new String(contract.getHashableData());
		String senPubK = key.getPublicKey().toString();
		es.removeListener(TITLE+contractId+senPubK);
		es.setListener("title", TITLE+contractId, TITLE+contractId+senPubK, new EstablisherServiceListener() {
			@Override
			public void notify(String title, String msg, String senderId) {
				// Checks if the message is a PCS, if yes store it in "pcs[round][k]"
				verifyAndStoreSignature(msg, senderId);
	
				// Check if the round is complete
				boolean claimFormed = true;
				for (int k=0; k<N; k++){
					if (round < N+2 && sigmaEstablisher.sigmaEstablisherData.getRoundReceived()[round][k] == null)
						claimFormed= false;
				}
				
				/*	Send the rounds (if we have the claim needed):
				 *  	We do a loop because sometimes, we receive the PCS for round+1 before the one for the current round
				 */  
				if (round == failingRound && !resolvedSent){
					sigmaEstablisher.resolvingStep.sendMessage();
					resolvedSent = true;
				}
				while (round<Math.min(N+2, failingRound) && claimFormed){
					round++;
					sendMessage();
					if (round == failingRound && !resolvedSent){
						sigmaEstablisher.resolvingStep.sendMessage();
						resolvedSent = true;
					}else{
						for (int k=0; k<N; k++){
							if (round < N+2 && sigmaEstablisher.sigmaEstablisherData.getRoundReceived()[round][k] == null)
								claimFormed= false;
						}
					}
				}
			}
		}, uris != null);
	}

}
