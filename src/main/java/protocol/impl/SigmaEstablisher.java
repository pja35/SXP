package protocol.impl;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;

import crypt.impl.signatures.ElGamalSignature;
import crypt.impl.signatures.ElGamalSigner;
/* TODO : Respect design pattern factory here
 * 		Problem here is that the implementation use other keys than the main one
 * 		Same problem in Trent
 */
import crypt.impl.signatures.SigmaSignature;
import crypt.impl.signatures.SigmaSigner;

import controller.Application;
import controller.tools.JsonTools;
import crypt.api.encryption.Encrypter;
import crypt.factories.EncrypterFactory;
import crypt.factories.SignerFactory;
import model.api.Status;
import model.entity.ContractEntity;
import model.entity.ElGamalKey;
import model.entity.sigma.Or;
import network.api.EstablisherService;
import network.api.EstablisherServiceListener;
import network.api.Peer;
import protocol.api.Establisher;
import protocol.impl.sigma.PCS;
import protocol.impl.sigma.Sender;
import protocol.impl.sigma.SigmaContract;


/** 
 *	Establisher for sigma protocol
 *
 * @author Nathanaël EON
 *
 * TODO : Change the messaging system to an asymetric one
 */

public class SigmaEstablisher extends Establisher<BigInteger, ElGamalKey, SigmaSignature, SigmaSigner, SigmaContract> {
	
	public static final String STARTING_MESSAGE = "START";
	public static final String TRENT_MESSAGE = "TRENT";
	public static final String FOR_TRENT_MESSAGE = "FOR TRENT";
	public static final String SIGNING_MESSAGE = "SIGNING_DATA";
	
	protected ElGamalKey trentK;
	protected EstablisherService establisherService =(EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);
	protected Peer peer = Application.getInstance().getPeer();

	
	// Store the different rounds of the signing protocol
	protected Or[][] promRoundSender;
	// Choose whether to use message or advertisement
	
	// Id for the contract (make sure we sign the same)
	protected String contractId;
	// Key of the parties (contract.getParties())
	protected ArrayList<ElGamalKey> keys;
	// sender public Key
	protected BigInteger senPubK;
	// Number of signers (protocol will have N+2 rounds)
	protected int N;
	// Know which parties are ready to sign
	protected String[] ready;
	// Current signature round
	protected int round = -1;
	// The PCS maker
	protected PCS pcs; 
	
	
	/**
	 * Setup the signing protocol
	 * @param <senderK> : elgamalkey (public and private) of the user
	 * @param <uri> : parties matching uri
	 */
	// TODO : REMOVE trentK
	public SigmaEstablisher(ElGamalKey senderK, ElGamalKey t, HashMap<ElGamalKey,String> uris){
		this.signer = SignerFactory.createSigmaSigner();
		this.signer.setKey(senderK);
		
		this.uris = uris;
		
		this.trentK = t;
	}
	
	/**
	 * @param <c> : contract to be signed
	 */
	@Override
	public void initialize(SigmaContract c){
		contract = c;
		
		/*
		 *Setup the field 
		 */
		contractId = new String(contract.getHashableData());
		// The El Gamal keys of all parties 
		keys = contract.getParties();
		senPubK = signer.getKey().getPublicKey();
		
		N = keys.size();
		// Used to know wether everyone has yet accepted the contract or not
		ready = new String[N];
		// Store the PCS received
		promRoundSender = new Or[N+2][N];
		
		/*
		 * Get ready to start
		 * If an advertisement was or is received, we check the signature and it is stored and we wait until everyone has sent its starter
		 */
		establisherService.removeListener(STARTING_MESSAGE+contractId+senPubK.toString());
		establisherService.setListener("title", STARTING_MESSAGE+contractId, STARTING_MESSAGE+contractId+senPubK.toString(), new EstablisherServiceListener(){
			@Override
			public void notify(String title, String data, String senderId) {
				JsonTools<ElGamalSignature> json = new JsonTools<>(new TypeReference<ElGamalSignature>(){});
				ElGamalSignature signature = json.toEntity(data);
				
				BigInteger msgSenKey = new BigInteger(senderId);
	
				int i = 0;
				while (!(keys.get(i).getPublicKey().equals(msgSenKey))){i++;}
				
				// Prepare the elGamalSigner to check the data
				ElGamalSigner elGamalSigner = SignerFactory.createElGamalSigner();
				elGamalSigner.setKey(keys.get(i));

				if (elGamalSigner.verify(STARTING_MESSAGE.getBytes(), signature)){
					ready[i] = "";
					// Checks if everyone is ready
					if (Arrays.asList(ready).indexOf(null) == (-1) && round==-1){
						round++;
						chooseTrent();
					}
				
				}
			}
		}, uris != null);
	}
	
	/**
	 * Launch the protocol : tell everyone that the user is ready to sign (pressed signing button)
	 */
	@Override
	public void start(){
		JsonTools<ElGamalSignature> json = new JsonTools<>(new TypeReference<ElGamalSignature>(){});
		
		ElGamalSigner elGamalSigner = SignerFactory.createElGamalSigner();
		elGamalSigner.setKey(signer.getKey());
		// Creates signature on message "start" and change it to Json
		String content = json.toJson(elGamalSigner.sign((STARTING_MESSAGE).getBytes()));

		// Sending an advertisement
		establisherService.sendContract(STARTING_MESSAGE+contractId, content, senPubK.toString(), peer, uris);
		int i=0;
		while (!(keys.get(i).getPublicKey().equals(senPubK))){i++;}
		ready[i] = "";
		if (Arrays.asList(ready).indexOf(null) == (-1) && round == -1){
			round++;
			chooseTrent();
		}
	}

	/**
	 * Choose Trent, put a listener for him, then start signing
	 */
	protected void chooseTrent(){
		establisherService.removeListener(STARTING_MESSAGE+contractId+senPubK.toString());
		
		// TODO : choose trent and associate its uri in uris
		contract.setTrentKey(trentK);
		signer.setTrentK(trentK);
		
		
		// Put a listener on Trent in case something goes wrong
		establisherService.removeListener(TRENT_MESSAGE+contractId+senPubK.toString());
		establisherService.setListener("title", TRENT_MESSAGE + contractId, TRENT_MESSAGE+contractId+senPubK.toString(), new EstablisherServiceListener(){
			@Override
			public void notify(String title, String data, String senderId) {
				// If the message is for another contract or by someone else thant Trent
				if (senderId.equals(trentK.getPublicKey().toString())){
					// If Trent found we were dishonest (second time a resolve sent)
					if (data.equals("Dishonest")){
						System.out.println("You were dishonest or request sent twice, third party didn't do nothing on this time");
					} 
					
					else{
						JsonTools<ArrayList<String>> jsons = new JsonTools<>(new TypeReference<ArrayList<String>>(){});
						ArrayList<String> answer = jsons.toEntity(data);

						// Making sure the message is from Trent
						signer.setTrentK(trentK);
						JsonTools<SigmaSignature> json = new JsonTools<>(new TypeReference<SigmaSignature>(){});

						JsonTools<HashMap<String,String>> jsonH = new JsonTools<>(new TypeReference<HashMap<String,String>>(){});
						HashMap<String,String> signatures = jsonH.toEntity(answer.get(2));
						
						if(signer.verify(answer.get(1).getBytes() ,json.toEntity(signatures.get(senPubK.toString())))){
							// If Trent aborted the contract
							if (answer.get(0).equals("aborted") || answer.get(0).equals("honestyToken")){
								setStatus(Status.CANCELLED);
								System.out.println("Signature cancelled");
								establisherService.removeListener(SIGNING_MESSAGE+contractId+senPubK.toString());
							}
							
							// If Trent solved the problem
							else if (answer.get(0).equals("resolved")){
								JsonTools<ArrayList<SigmaSignature>> jsonSignatures = new JsonTools<>(new TypeReference<ArrayList<SigmaSignature>>(){});
								ArrayList<SigmaSignature> sigSign = jsonSignatures.toEntity(answer.get(1));

								
								// Check the signatures (we don't if it was on round -1 or -2)
								byte[] data1 = (new String(contract.getHashableData()) + (round - 1)).getBytes();
								byte[] data2 = (new String(contract.getHashableData()) + (round - 2)).getBytes();
								
								for (SigmaSignature signature : sigSign){
									signer.setReceiverK(keys.get(sigSign.indexOf(signature)));
									
									if (signer.verify(data1, signature) || signer.verify(data2, signature)){
										contract.addSignature(keys.get(sigSign.indexOf(signature)), signature);
									}
								}
								
								if (contract.isFinalized()){
									setStatus(Status.FINALIZED);
									System.out.println("CONTRACT FINALIZED");
									establisherService.removeListener(SIGNING_MESSAGE+contractId+senPubK.toString()); 
								}
							}
						}
						
					}
				}
			}
		}, false);
		
		
		// Sometimes, Trent cancel before we start signing ...
		if (getStatus() != Status.CANCELLED && getStatus() != Status.FINALIZED)
			sign();
	}
	
	/**
	 * The contract signing protocol
	 * TODO : Setup a timer that will trigger resolve()
	 */
	protected void sign(){
		setStatus(Status.SIGNING);
	
		// Necessary tools to create the PCS
		Sender sender = new Sender(signer.getKey());
		pcs = new PCS(sender, trentK);
		round = 1;

		establisherService.removeListener(SIGNING_MESSAGE+contractId+senPubK.toString());
		establisherService.setListener("title", SIGNING_MESSAGE+contractId, SIGNING_MESSAGE+contractId+senPubK.toString(), new EstablisherServiceListener() {
			@Override
			public void notify(String title, String msg, String senderId) {
				// Checks if the message is a PCS, if yes store it in "pcs[round][k]"
				verifyAndStoreSignature(msg, senderId);

				// Check if the round is complete
				boolean claimFormed = true;
				for (int k=0; k<N; k++){
					if (round < N+2 && promRoundSender[round][k] == null)
						claimFormed= false;
				}
				
				/*	Send the rounds (if we have the claim needed):
				 *  	We do a loop because sometimes, we receive the PCS for round+1 before the one for the current round
				 */  
				while (round<=(N+1) && claimFormed){
					sendRound(++round);
					for (int k=0; k<N; k++){
						if (round < N+2 && promRoundSender[round][k] == null)
							claimFormed= false;
					}
				}
			}
		}, uris != null);
		
		// Send the first round
		sendRound(1);
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
		establisherService.removeListener(SIGNING_MESSAGE+contractId+senPubK.toString());
		
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
			String claimK = json.toJson(promRoundSender[round-1], true);
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
		establisherService.sendContract(FOR_TRENT_MESSAGE + trentK.getPublicKey().toString(), fullContent, senPubK.toString(), peer, null);
	}
	
	/*
	 * Send the needed message to do the protocol, called in sign() method
	 * @param round : round we are at
	 * @param uris : the destination peers uris
	 */
	protected void sendRound(int round){
		// Content of the message which will be sent
		HashMap<String, String> content = new HashMap<String, String>();
		
		for (int k=0; k<N; k++){

			// Public key of the receiver
			ElGamalKey receiverK = keys.get(k);
			
			// If the receiver is the sender, isSender = true 
			boolean isSender = receiverK.getPublicKey().equals(senPubK);
			
			// On the last round, send the clear signature
			if (round==(N+2)){
				JsonTools<SigmaSignature> json = new JsonTools<>(new TypeReference<SigmaSignature>(){});
				SigmaSignature signature = pcs.getClearSignature(contract, receiverK);
				if (isSender)
					contract.addSignature(keys.get(k), signature);
				content.put(receiverK.getPublicKey().toString(), json.toJson(signature, true));
			// Otherwise send round k
			}else {
				byte[] data = (new String(contract.getHashableData()) + round).getBytes();
				Or p = pcs.getPcs(data, receiverK, true);
				content.put(receiverK.getPublicKey().toString(), encryptMsg(getJson(p), receiverK));
				if (isSender){
					promRoundSender[round][k] = p;
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
		while (!(keys.get(i).getPublicKey().equals(senPubK))){i++;}
		System.out.println("Sending Round : " + round + " : by " + i);
		
		// Sending an advertisement
		establisherService.sendContract(SIGNING_MESSAGE+contractId, dataToBeSent, senPubK.toString(), peer, uris);
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
		// Get the keys of the sender of the message
		BigInteger msgSenKey = new BigInteger(pubK);
		int i = 0;
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
				s.setTrentK(trentK);
				
				if (pcs.verifySignature(signature, s, contract)) {
					contract.addSignature(keys.get(i), signature);
					if (contract.isFinalized()){
						int j = 0;
						while (!(keys.get(j).getPublicKey().equals(senPubK))){j++;}
						System.out.println("--- CONTRACT FINALIZED -- id : " + j);
						setStatus(Status.FINALIZED);
						establisherService.removeListener(SIGNING_MESSAGE + contractId + senPubK.toString());
					}
				}
			// Otherwise, test if it is the correct PCS, if so : store it
			}else if (promRoundSender[k][i] == null){
				String msg = decryptMsg(content.get(senPubK.toString()), signer.getKey());
				byte[] data = (new String(contract.getHashableData()) + k).getBytes();
				if (getPrivateCS(msg).Verifies(data)){
					promRoundSender[k][i]=getPrivateCS(msg);
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
