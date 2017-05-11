package protocol.impl;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;

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
import model.api.Status;
import model.entity.ContractEntity;
import model.entity.ElGamalKey;
import model.entity.sigma.Or;
import network.api.EstablisherService;
import network.api.Messages;
import network.api.ServiceListener;
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
	
	
	protected ElGamalKey trentK;
	protected final EstablisherService establisherService =(EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);
	
	// Store the different rounds of the signing protocol
	protected Or[][] promRoundSender;
	
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
	protected int round = 0;
	// The PCS maker
	protected PCS pcs; 
	
	
	
	/**
	 * Setup the signing protocol
	 * @param <senderK> : elgamalkey (public and private) of the user
	 * @param <uri> : parties matching uri
	 */
	// TODO : REMOVE trentK
	public SigmaEstablisher(ElGamalKey senderK, HashMap<ElGamalKey, String> uri, ElGamalKey t){
		signer = new SigmaSigner();
		signer.setKey(senderK);
		uris = uri;
		trentK = t;
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
		 * If we receive a starting message, it is stored and we wait until everyone has sent
		 * 		its starting message
		 */
		establisherService.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {// Finding the sender
				BigInteger msgSenKey = new BigInteger(messages.getMessage("sourceId"));
				int i = 0;
				while (!(keys.get(i).getPublicKey().equals(msgSenKey))){i++;}
				
				if (decryptMsg(messages.getMessage("contract"),signer.getKey()).equals("start") &&
					messages.getMessage("title").equals(contractId)){
					ready[i] = "";
					// Checks if everyone is ready
					if (Arrays.asList(ready).indexOf(null) == (-1)){
						chooseTrent();
					}
				}
			}
		}, senPubK.toString());
	}
	
	/**
	 * Launch the protocol : tell everyone that the user is ready to sign (pressed signing button)
	 */
	@Override
	public void start(){
		for (int k=0; k<N; k++){
			ElGamalKey key = keys.get(k);
			establisherService.sendContract(contractId,
											key.getPublicKey().toString(), 
											senPubK.toString(),
											encryptMsg("start",key),
											uris.get(key));
		}
	}

	/**
	 * Choose Trent, put a listener for him, then start signing
	 */
	protected void chooseTrent(){
		// TODO : choose trent and associate its uri in uris
		contract.setTrentKey(trentK);
		signer.setTrentK(trentK);
		
		
		// Put a listener on Trent in case something goes wrong
		establisherService.addListener(new ServiceListener(){
			@Override
			public void notify(Messages messages){
				
				// If the message is for another contract
				if (messages.getMessage("title").equals(contractId)){
					
					// If Trent found we were dishonest (second time a resolve sent)
					if (messages.getMessage("contract").equals("Dishonest")){
						System.out.println("You were dishonest, third party didn't do nothing");
					} 
					
					else{
						JsonTools<ArrayList<String>> jsons = new JsonTools<>(new TypeReference<ArrayList<String>>(){});
						ArrayList<String> answer = jsons.toEntity(messages.getMessage("contract"));

						// Making sure the message is from Trent
						signer.setTrentK(trentK);
						JsonTools<SigmaSignature> json = new JsonTools<>(new TypeReference<SigmaSignature>(){});

						if(signer.verify(answer.get(1).getBytes() ,json.toEntity(answer.get(2)))){
							// If Trent aborted the contract
							if (answer.get(0).equals("aborted") || answer.get(0).equals("honestyToken")){
								setStatus(Status.CANCELLED);
								System.out.println("Signature cancelled");
							}
							
							// If Trent solved the problem
							else if (answer.get(0).equals("resolved")){
								JsonTools<ArrayList<SigmaSignature>> jsonSignatures = new JsonTools<>(new TypeReference<ArrayList<SigmaSignature>>(){});
								ArrayList<SigmaSignature> sigSign = jsonSignatures.toEntity(answer.get(1));

								
								// Check the signatures (we don't if it was on round -1 or -2)
								byte[] data = (new String(contract.getHashableData()) + (round - 1)).getBytes();
								byte[] data2 = (new String(contract.getHashableData()) + (round - 2)).getBytes();
								
								for (SigmaSignature signature : sigSign){
									signer.setReceiverK(keys.get(sigSign.indexOf(signature)));
									
									if (signer.verify(data, signature) || signer.verify(data2, signature)){
										contract.addSignature(keys.get(sigSign.indexOf(signature)), signature);
									}
								}
								
								if (contract.isFinalized()){
									setStatus(Status.FINALIZED);
									System.out.println("CONTRACT FINALIZED"); 
								}
							}
						}
						
					}
				}
			}
		}, senPubK + "TRENT");
		
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
						if (promRoundSender[round][k] == null)
							claimFormed= false;
					}
					
					/*	Send the rounds (if we have the claim needed):
					 *  	We do a loop because sometimes, we receive the PCS for round+1 before the one for the current round
					 */  
					while (round<=(N+1) && claimFormed){
						sendRound(++round);
						for (int k=0; k<N; k++){
							if (promRoundSender[round][k] == null)
								claimFormed= false;
						}
					}
				}
			}
		}, senPubK.toString());

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

		establisherService.removeListener(senPubK.toString());
		
		String[] content = new String[5];

		// Round
		content[0] = String.valueOf(round-1);
		// Uris
		JsonTools<HashMap<BigInteger, String>> json1 = new JsonTools<>(new TypeReference<HashMap<BigInteger, String>>(){});
		HashMap<BigInteger, String> u = new HashMap<BigInteger, String>();
		for (ElGamalKey ke : uris.keySet()){
			u.put(ke.getPublicKey(), uris.get(ke));
		}
		content[1] = json1.toJson(u);
		
		
		// Contract
		JsonTools<ContractEntity> json2 = new JsonTools<>(new TypeReference<ContractEntity>(){});
		content[2] = json2.toJson(contract.getEntity(),false);
		
		// Claim(k)
		signer.setReceiverK(trentK);
		SigmaSignature sigClaimK;
		if (round<=1){
			content[3] = encryptMsg("ABORT", trentK);
			sigClaimK = signer.sign("ABORT".getBytes());
		}else {
			JsonTools<Or[]> json = new JsonTools<>(new TypeReference<Or[]>(){});
			String claimK = json.toJson(promRoundSender[round-1], true);
			content[3] = encryptMsg(claimK, trentK);
			sigClaimK = signer.sign(claimK.getBytes());
		}
		JsonTools<SigmaSignature> json3 = new JsonTools<>(new TypeReference<SigmaSignature>(){});
		content[4] = encryptMsg(json3.toJson(sigClaimK, false), trentK);
		
		// Concatenate the content
		JsonTools<String[]> json = new JsonTools<>(new TypeReference<String[]>(){});
		String fullContent = json.toJson(content, false);

		System.out.println("--- Sending resolve request to Trent --- Round : " + (round-1));
		
		establisherService.sendContract(contractId,
							trentK.getPublicKey().toString()+"TRENT",
							senPubK.toString(),
							fullContent,
							uris.get(trentK));
	}
	
	/*
	 * Send the needed message to do the protocol, called in sign() method
	 * @param round : round we are at
	 * @param uris : the destination peers uris
	 */
	protected void sendRound(int round){
		// Loop : send to every party the correct message
		for (int k=0; k<N; k++){

			// Public key of the receiver
			ElGamalKey receiverK = keys.get(k);
			
			// If the receiver is the sender, isSender = true 
			boolean isSender = receiverK.getPublicKey().equals(senPubK);

			// Content of the message which will be sent
			String content;
			
			// On the last round, send the clear signature
			if (round==(N+2)){
				JsonTools<SigmaSignature> json = new JsonTools<>(new TypeReference<SigmaSignature>(){});
				SigmaSignature signature = pcs.getClearSignature(contract, receiverK);
				if (isSender)
					contract.addSignature(keys.get(k), signature);
				content = json.toJson(signature, true);
			// Otherwise send round k
			}else {
				byte[] data = (new String(contract.getHashableData()) + round).getBytes();
				Or p = pcs.getPcs(data, receiverK, true);
				content=encryptMsg(getJson(p), receiverK);
				if (isSender){
					promRoundSender[round][k] = p;
				}
			} 
			
			// No need to send if we are the receiver
			if (!isSender){
				// Encapsulate the data we shall send (round + content)
				String[] fullContent = {String.valueOf(round), content};
				JsonTools<String[]> json2 = new JsonTools<>(new TypeReference<String[]>(){});
				String fullContentS = json2.toJson(fullContent);

				// Getting the sender public key index 
				int i = 0;
				while (!(keys.get(i).getPublicKey().equals(senPubK))){i++;}
				System.out.println("Sending Round : " + round + " - for " + k + " : by " + i);
				
				establisherService.sendContract(contractId, 
									receiverK.getPublicKey().toString(),
									senPubK.toString(),
									fullContentS,
									uris.get(receiverK));
			}
		}
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
		JsonTools<String[]> json0 = new JsonTools<>(new TypeReference<String[]>(){});
		String[] fullContent = json0.toEntity(message, true);
		
		int k= Integer.parseInt(fullContent[0]);
		String content = fullContent[1];
		
		// Don't do anything if the sender is the actual user (shouldn't happen though)
		if (!(senderKey.getPublicKey().equals(senPubK))){
			// If it's the last round, test the clear signature
			if (k == (N+2)){
				JsonTools<SigmaSignature> json = new JsonTools<>(new TypeReference<SigmaSignature>(){});
				SigmaSignature signature =  json.toEntity(content, true);
				
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
					}
				}
			// Otherwise, test if it is the correct PCS, if so : store it
			}else {
				String msg = decryptMsg(content, signer.getKey());
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
