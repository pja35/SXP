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
import crypt.impl.signatures.SigmaSigner;
import model.api.Status;
import model.api.Wish;
import model.entity.ElGamalKey;
import model.entity.sigma.Or;
import model.entity.sigma.SigmaSignature;
import network.api.EstablisherService;
import network.api.EstablisherServiceListener;
import network.api.Peer;
import protocol.impl.SigmaEstablisher;
import protocol.impl.sigma.PCS;
import protocol.impl.sigma.Sender;
import protocol.impl.sigma.SigmaContract;

/**
 * The signing step, sigma protocol happens here
 * @author Nathanaël Eon
 *
 *	Data format : HashMap<String,String>
 *		It contains the round of the protocol (Map.get("round"))
 *		It contains the different signatures, correctly encrypted for each receiver using publicKey as Map Key
 */
public class ProtocolSign implements ProtocolStep {

	public final static String TITLE = "SIGNING";

	@XmlElement(name="round")
	protected int round;
	
	@XmlElement(name="key")
	protected ElGamalKey key;
	
	protected SigmaEstablisher sigmaEstablisher;
	protected EstablisherService es;
	protected Peer peer;
	protected HashMap<ElGamalKey,String> uris;
	protected SigmaContract contract;
	protected int N;
	protected PCS pcs;
	
	
	@JsonCreator
	public ProtocolSign(@JsonProperty("key") ElGamalKey key, @JsonProperty("round") int round){
		this.key = key;
		this.round = round;

		Sender sender = new Sender(key);
		pcs = new PCS(sender, sigmaEstablisher.sigmaEstablisherData.getTrentKey());
	}
	
	public ProtocolSign(SigmaEstablisher sigmaE, 
			ElGamalKey key){
		this.sigmaEstablisher = sigmaE;
		this.key = key;
		
		this.es = sigmaE.establisherService;
		this.peer = sigmaE.peer;
		this.uris = sigmaE.sigmaEstablisherData.getUris();
		this.contract = sigmaE.sigmaEstablisherData.getContract();
		this.N = this.contract.getParties().size();
		sigmaEstablisher.sigmaEstablisherData.setRoundReceived(new Or[N+2][N]);
		
		Sender sender = new Sender(key);
		pcs = new PCS(sender, sigmaEstablisher.sigmaEstablisherData.getTrentKey());
		round = 1;
		
		this.setupListener();
	}
	
	
	@Override
	public void restore(SigmaEstablisher sigmaE){
		this.sigmaEstablisher = sigmaE;
		this.es = sigmaE.establisherService;
		this.peer = sigmaE.peer;
		this.uris = sigmaE.sigmaEstablisherData.getUris();
		this.contract = sigmaE.sigmaEstablisherData.getContract();
		this.N = this.contract.getParties().size();
		
		this.setupListener();
	}
	
	
	@Override
	public String getName(){
		return TITLE;
	}
	
	@Override
	public int getRound(){
		return this.round;
	}
	
	@Override
	public void sendMessage() {
		// Check the wish of the user each time we send a message
		if (contract.getWish().equals(Wish.REFUSE)){
			this.sigmaEstablisher.resolvingStep.sendMessage();
		}else{
			// Content of the message which will be sent
			HashMap<String, String> content = new HashMap<String, String>();
			BigInteger senPubK = key.getPublicKey();
			
			for (int k=0; k<N; k++){
	
				// Public key of the receiver
				ElGamalKey receiverK = contract.getParties().get(k);
				
				// If the receiver is the sender, isSender = true 
				boolean isSender = receiverK.getPublicKey().equals(senPubK);
				
				// On the last round, send the clear signature
				if (round==(N+2)){
					JsonTools<SigmaSignature> json = new JsonTools<>(new TypeReference<SigmaSignature>(){});
					SigmaSignature signature = pcs.getClearSignature(contract, receiverK);
					if (isSender)
						contract.addSignature(contract.getParties().get(k), signature);
					content.put(receiverK.getPublicKey().toString(), json.toJson(signature, true));
				// Otherwise send round k
				}else {
					byte[] data = (new String(contract.getHashableData()) + round).getBytes();
					Or p = pcs.getPcs(data, receiverK, true);
					content.put(receiverK.getPublicKey().toString(), encryptMsg(getJson(p), receiverK));
					if (isSender){
						sigmaEstablisher.sigmaEstablisherData.getRoundReceived()[round][k] = p;
					}
				} 
			}
			
			// Adding the round to data sent
			content.put("ROUND", String.valueOf(round));
			
			// Convert map to String
			JsonTools<HashMap<String,String>> json2 = new JsonTools<>(new TypeReference<HashMap<String,String>>(){});
			String dataToBeSent = json2.toJson(content);
			
			// Getting the sender public key index 
			int i = 0;
			while (!(contract.getParties().get(i).getPublicKey().equals(senPubK))){i++;}
			System.out.println("Sending Round : " + round + " : by " + i);
			
			// Sending an advertisement
			es.sendContract(TITLE+new String(contract.getHashableData()), dataToBeSent, senPubK.toString(), peer, uris);
		}
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
				while (round<=(N+1) && claimFormed && contract.getStatus().equals(Status.SIGNING)){
					round++;
					sendMessage();
					for (int k=0; k<N; k++){
						if (round < N+2 && sigmaEstablisher.sigmaEstablisherData.getRoundReceived()[round][k] == null)
							claimFormed= false;
					}
				}
			}
		}, uris != null);
	}

	@Override
	public void stop(){
		String contractId = new String(contract.getHashableData());
		String senPubK = key.getPublicKey().toString();
		es.removeListener(TITLE+contractId+senPubK);
	}
	
	

	/*
	 * Verify the message received (if the message is the last, check if the signature is ok)
	 * 		called in sign() method
	 * @param message : message we receive (messages.getMessage("contract"))
	 * @param pubK : the sender ElGamal public key
	 * @param pcs : 
	 * @return
	 */
	protected void verifyAndStoreSignature(String message, String pubK){
		BigInteger senPubK = key.getPublicKey();		
		
		// Get the keys of the sender of the message
		BigInteger msgSenKey = new BigInteger(pubK);
		int i = 0;
		ArrayList<ElGamalKey> keys = contract.getParties();
		while (!(keys.get(i).getPublicKey().equals(msgSenKey))){i++;}
		ElGamalKey senderKey = keys.get(i);

		// From json message to the object {"k", PCS}
		JsonTools<HashMap<String,String>> json0 = new JsonTools<>(new TypeReference<HashMap<String,String>>(){});
		HashMap<String,String> content = json0.toEntity(message);
		
		int k= Integer.parseInt(content.get("ROUND"));
		// Don't do anything if the sender is the actual user (shouldn't happen though)
		if (!(senderKey.getPublicKey().equals(senPubK))){
			// If it's the last round, test the clear signature
			if (k == (N+2)){
				JsonTools<SigmaSignature> json2 = new JsonTools<>(new TypeReference<SigmaSignature>(){});
				SigmaSignature signature =  json2.toEntity(content.get(senPubK.toString()), true);
				
				SigmaSigner s = new SigmaSigner();
				ElGamalKey ke = new ElGamalKey();
				ke.setPublicKey(senPubK);
				s.setKey(ke);
				s.setTrentK(sigmaEstablisher.sigmaEstablisherData.getTrentKey());
				
				if (pcs.verifySignature(signature, s, contract)) {
					contract.addSignature(keys.get(i), signature);
					if (contract.isFinalized()){
						int j = 0;
						while (!(keys.get(j).getPublicKey().equals(senPubK))){j++;}
						System.out.println("--- CONTRACT FINALIZED -- id : " + j);
						sigmaEstablisher.setStatus(Status.FINALIZED);
						es.removeListener(TITLE + new String(contract.getHashableData()) + senPubK.toString());
					}
				}
			// Otherwise, test if it is the correct PCS, if so : store it
			}else if (sigmaEstablisher.sigmaEstablisherData.getRoundReceived()[k][i] == null){
				String msg = decryptMsg(content.get(senPubK.toString()), key);
				byte[] data = (new String(contract.getHashableData()) + k).getBytes();
				if (getPrivateCS(msg).Verifies(data)){
					sigmaEstablisher.sigmaEstablisherData.getRoundReceived()[k][i]=getPrivateCS(msg);
				}
			}
		}
	}
	

	/*
	 * Primitives
	 * 		Transformation json <-> object
	 * 		Decrypting <-> Encrypting
	 */
	
	// Return the string representing the private contract signature
	protected String getJson(Or pcs){
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		return json.toJson(pcs, true);
	}
	// Return the PCS (Or Object) from json
	protected Or getPrivateCS(String pcs){
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		return json.toEntity(pcs, true);
	}
	
	// Return the message encrypted with public key
	protected String encryptMsg(String msg, ElGamalKey key){
		Encrypter<ElGamalKey> encrypter = EncrypterFactory.createElGamalSerpentEncrypter();
		encrypter.setKey(key);
		return new String(encrypter.encrypt(msg.getBytes()));
	}
	// Return the message decrypted with private key 
	protected String decryptMsg(String msg, ElGamalKey key){
		Encrypter<ElGamalKey> encrypter = EncrypterFactory.createElGamalSerpentEncrypter();
		encrypter.setKey(key);
		return new String(encrypter.decrypt(msg.getBytes()));
	}
}
