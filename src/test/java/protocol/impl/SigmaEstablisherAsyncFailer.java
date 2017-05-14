package protocol.impl;


import model.api.Status;
import model.entity.ElGamalKey;
import network.api.EstablisherServiceListener;
import network.api.advertisement.EstablisherAdvertisementInterface;
import protocol.impl.sigma.PCS;
import protocol.impl.sigma.Sender;

/**
 * 
 * @author Nathanaël Eon
 *
 * Hack of SigmaEstablisher to produce fails
 */
public class SigmaEstablisherAsyncFailer extends SigmaEstablisherAsync{

	private int failingRound;
	private boolean liar;
	
	public SigmaEstablisherAsyncFailer(ElGamalKey k, ElGamalKey t, int f, boolean liar) {
		super(k, t);
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

		establisherService.removeListens(SIGNING_MESSAGE+contractId+senPubK);
		establisherService.listens("title", SIGNING_MESSAGE+contractId, SIGNING_MESSAGE+contractId+senPubK, new EstablisherServiceListener() {
			@Override
			public void notify(EstablisherAdvertisementInterface adv) {
				String msg = adv.getContract();
				// Checks if the message is a PCS, if yes store it in "pcs[round][k]"
				verifyAndStoreSignature(msg, adv.getKey());
				

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
		});

		// Send the first round
		sendRound(1);
	}
}
