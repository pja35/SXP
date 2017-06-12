package protocol.impl.sigma.step;

import java.math.BigInteger;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import model.entity.ContractEntity;
import model.entity.ElGamalKey;
import model.entity.sigma.Or;
import model.entity.sigma.SigmaSignature;
import protocol.impl.SigmaEstablisher;
import protocol.impl.sigma.steps.ProtocolResolve;
import protocol.impl.sigma.steps.ProtocolStep;

public class ProtocolResolveFailer extends ProtocolResolve {

	public ProtocolResolveFailer(SigmaEstablisher sigmaE, ElGamalKey key) {
		super(sigmaE, key);
	}


	@Override
	public void sendMessage() {
		ProtocolStep step = sigmaEstablisher.sigmaEstablisherData.getProtocolStep();
		
		int round = step.getRound();
		BigInteger senPubK = key.getPublicKey();
		ElGamalKey trentK = sigmaEstablisher.sigmaEstablisherData.getTrentKey();

		String[] content = new String[4];

		// Round
		content[0] = String.valueOf(round-1);
		
		
		// Contract
		JsonTools<ContractEntity> json2 = new JsonTools<>(new TypeReference<ContractEntity>(){});
		content[1] = json2.toJson(contract.getEntity(),false);
		
		// Claim(k)
		signer.setReceiverK(trentK);
		SigmaSignature sigClaimK;
		if (round<=1){
			content[2] = encryptMsg("ABORT", trentK);
			sigClaimK = signer.sign("ABORT".getBytes());
		}else {
			JsonTools<Or[]> json = new JsonTools<>(new TypeReference<Or[]>(){});
			String claimK = json.toJson(sigmaEstablisher.sigmaEstablisherData.getRoundReceived()[round-1], true);
			content[2] = encryptMsg(claimK, trentK);
			sigClaimK = signer.sign(claimK.getBytes());
		}
		JsonTools<SigmaSignature> json3 = new JsonTools<>(new TypeReference<SigmaSignature>(){});
		content[3] = encryptMsg(json3.toJson(sigClaimK, false), trentK);
		
		// Concatenate the content
		JsonTools<String[]> json = new JsonTools<>(new TypeReference<String[]>(){});
		String fullContent = json.toJson(content, false);

		System.out.println("--- Sending resolve request to Trent --- Round : " + (round-1));

		// For Trent, use only Advertisement
		es.sendContract(TITLE + trentK.getPublicKey().toString(), fullContent, senPubK.toString(), peer, null);
	}

	
	@Override
	public void setupListener() {}
}
