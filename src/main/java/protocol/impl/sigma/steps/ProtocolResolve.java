package protocol.impl.sigma.steps;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import crypt.api.encryption.Encrypter;
import crypt.factories.EncrypterFactory;
import crypt.factories.SignerFactory;
import crypt.impl.signatures.SigmaSigner;
import model.api.Status;
import model.entity.ContractEntity;
import model.entity.ElGamalKey;
import model.entity.sigma.Or;
import model.entity.sigma.SigmaSignature;
import network.api.EstablisherService;
import network.api.EstablisherServiceListener;
import network.api.Peer;
import protocol.impl.SigmaEstablisher;
import protocol.impl.sigma.SigmaContract;
import protocol.impl.sigma.Trent;

/**
 * This step is used if something goes wrong. It stop the protocol and contact the TTP
 * @author Nathanaël Eon
 *
 * Format of data Sent : String[4]
 * 		data[0] = round
 * 		data[1] = contract
 * 		data[2] = request (abort / encryptForTrent(claim)
 * 		data[3] = Sigma Signature of the request
 */
public class ProtocolResolve implements ProtocolStep {

	public static final String TITLE = "RESOLVE";
	
	@XmlElement(name="key")
	protected ElGamalKey key;
	@XmlElement(name="resolveSent")
	protected boolean resolveSent = false;
	
	protected SigmaEstablisher sigmaEstablisher;
	protected EstablisherService es;
	protected Peer peer;
	protected SigmaContract contract;
	protected SigmaSigner signer;
	
	
	
	@JsonCreator
	public ProtocolResolve(@JsonProperty("key") ElGamalKey key,
			@JsonProperty("resolveSent") boolean resolveSent){
		this.key = key;
		this.signer = SignerFactory.createSigmaSigner();
		this.signer.setKey(key);
		
		this.resolveSent = resolveSent;
	}
	
	public ProtocolResolve(SigmaEstablisher sigmaE, 
			ElGamalKey key){
		this.key = key;
		this.signer = SignerFactory.createSigmaSigner();
		this.signer.setKey(key);
		

		this.sigmaEstablisher = sigmaE;
		this.es = sigmaE.establisherService;
		this.peer = sigmaE.peer;
		this.contract = sigmaE.sigmaEstablisherData.getContract();
		this.signer.setTrentK(sigmaE.sigmaEstablisherData.getTrentKey());
		
		this.setupListener();
	}
	
	@Override
	public void restore(SigmaEstablisher sigmaE){
		this.sigmaEstablisher = sigmaE;
		this.es = sigmaE.establisherService;
		this.peer = sigmaE.peer;
		this.contract = sigmaE.sigmaEstablisherData.getContract();
		this.signer.setTrentK(sigmaE.sigmaEstablisherData.getTrentKey());
		
		this.setupListener();
	}
	
	@Override
	public String getName() {
		return TITLE;
	}
	
	/**
	 * Return -2 if the resolved has been sent; 0 otherwise
	 */
	@Override
	public int getRound() {
		if (resolveSent)
			return -2;
		else
			return 0;
	}

	@Override
	public void sendMessage() {
		ProtocolStep step = sigmaEstablisher.sigmaEstablisherData.getProtocolStep();
		sigmaEstablisher.setStatus(Status.RESOLVING);
		
		int round = step.getRound();
		BigInteger senPubK = key.getPublicKey();
		ElGamalKey trentK = sigmaEstablisher.sigmaEstablisherData.getTrentKey();
		
		step.stop();
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

		// For Trent, use only Advertisement
		es.sendContract(TITLE + trentK.getPublicKey().toString(), fullContent, senPubK.toString(), peer, null);

		System.out.println("--- Sending resolve request to Trent --- Round : " + (round-1));
	}

	@Override
	public void setupListener() {
		final String contractId = new String(contract.getHashableData());
		final BigInteger senPubK = key.getPublicKey();
		
		es.removeListener(Trent.TRENT_MESSAGE+contractId+senPubK.toString());
		es.setListener("title", Trent.TRENT_MESSAGE + contractId, Trent.TRENT_MESSAGE+contractId+senPubK.toString(), new EstablisherServiceListener(){
			@Override
			public void notify(String title, String data, String senderId) {
				ProtocolStep step = sigmaEstablisher.sigmaEstablisherData.getProtocolStep();

				// If the message is for another contract or by someone else thant Trent
				if (senderId.equals(sigmaEstablisher.sigmaEstablisherData.getTrentKey().getPublicKey().toString())){
					// If Trent found we were dishonest (second time a resolve sent)
					if (data.substring(0, 9).equals("dishonest")){
						System.out.println("Someone was dishonest : "+ data.substring(10) + "\nTrent decided not to do anything");
					} 
					
					else{
						JsonTools<ArrayList<String>> jsons = new JsonTools<>(new TypeReference<ArrayList<String>>(){});
						ArrayList<String> answer = jsons.toEntity(data);

						// Making sure the message is from Trent
						signer.setTrentK(sigmaEstablisher.sigmaEstablisherData.getTrentKey());
						JsonTools<SigmaSignature> json = new JsonTools<>(new TypeReference<SigmaSignature>(){});

						JsonTools<HashMap<String,String>> jsonH = new JsonTools<>(new TypeReference<HashMap<String,String>>(){});
						HashMap<String,String> signatures = jsonH.toEntity(answer.get(2));
						
						if(signer.verify(answer.get(1).getBytes() ,json.toEntity(signatures.get(senPubK.toString())))){
							// If Trent aborted the contract
							if (answer.get(0).equals("aborted") || answer.get(0).equals("honestyToken")){
								sigmaEstablisher.setStatus(Status.CANCELLED);
								System.out.println("Signature cancelled");
								es.removeListener(step.getName()+contractId+senPubK.toString());
								es.removeListener(TITLE+contractId+senPubK.toString()); 
							}
							
							// If Trent solved the problem
							else if (answer.get(0).equals("resolved")){
								
								JsonTools<ArrayList<SigmaSignature>> jsonSignatures = new JsonTools<>(new TypeReference<ArrayList<SigmaSignature>>(){});
								ArrayList<SigmaSignature> sigSign = jsonSignatures.toEntity(answer.get(1));

								
								// Check the signatures (we don't if it was on round -1 or -2)
								byte[] data1 = (new String(contract.getHashableData()) + (step.getRound())).getBytes();
								byte[] data2 = (new String(contract.getHashableData()) + (step.getRound() - 1)).getBytes();
								byte[] data3 = (new String(contract.getHashableData()) + (step.getRound() - 2)).getBytes();
								
								for (SigmaSignature signature : sigSign){
									signer.setReceiverK(contract.getParties().get(sigSign.indexOf(signature)));
									
									if (signer.verify(data1, signature) || signer.verify(data2, signature) || signer.verify(data3, signature)){
										contract.addSignature(contract.getParties().get(sigSign.indexOf(signature)), signature);
									}
								}
								
								if (contract.isFinalized()){
									sigmaEstablisher.setStatus(Status.FINALIZED);
									System.out.println("CONTRACT FINALIZED");
									es.removeListener(step.getName()+contractId+senPubK.toString()); 
									es.removeListener(TITLE+contractId+senPubK.toString()); 
								}
							}
						}
						
					}
				}
			}
		}, false);
	}
	
	@Override
	public void stop(){
		String contractId = new String(contract.getHashableData());
		String senPubK = key.getPublicKey().toString();
		es.removeListener(Trent.TRENT_MESSAGE+contractId+senPubK);
	}

	
	// Return the message encrypted with public key
	protected String encryptMsg(String msg, ElGamalKey key){
		Encrypter<ElGamalKey> encrypter = EncrypterFactory.createElGamalSerpentEncrypter();
		encrypter.setKey(key);
		return new String(encrypter.encrypt(msg.getBytes()));
	}

}
