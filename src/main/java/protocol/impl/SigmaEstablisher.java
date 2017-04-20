package protocol.impl;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.Application;
import controller.tools.JsonTools;
import crypt.api.encryption.Encrypter;
import crypt.api.signatures.Signer;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.factories.EncrypterFactory;
import crypt.factories.SignerFactory;
import crypt.impl.signatures.SigmaSignature;
import crypt.impl.signatures.SigmaSigner;
import model.api.Status;
import model.api.UserSyncManager;
import model.entity.ElGamalKey;
import model.entity.User;
import model.entity.sigma.Or;
import model.syncManager.UserSyncManagerImpl;
import network.api.EstablisherService;
import network.api.Messages;
import network.api.ServiceListener;
import protocol.api.Establisher;
import protocol.impl.sigma.PCS;
import protocol.impl.sigma.Sender;
import protocol.impl.sigma.SigmaContract;
import rest.api.Authentifier;


/** 
 *	Establisher for sigma protocol
 *
 * @author Nathanaël EON
 *
 * TODO : Change the messaging system to an asymetric one
 */

public class SigmaEstablisher extends Establisher<BigInteger, ElGamalKey, SigmaSignature, SigmaSigner, SigmaContract> {
	
	
	private ElGamalKey trentK;
	private final EstablisherService establisherService =(EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);
	// Store the different rounds of the signing protocol
	private Or[][] promRoundSender;
	
	
	// Id for the contract (make sure we sign the same)
	private String contractId;
	// Clauses that will be signed during the contract
	private String contractClauses;
	// Key of the parties (contract.getParties())
	private ArrayList<ElGamalKey> keys;
	// sender public Key
	private BigInteger senPubK;
	// Number of signers (protocol will have N+2 rounds)
	private int N;
	// Know which parties are ready to sign
	private String[] ready;
	// Current signature round
	private int round = 0;
	// The PCS maker
	private PCS pcs; 
	
	
	
	/**
	 * Beginning Constructor
	 * 		Setup the signing protocol
	 * @param <token> : token for authentifier (getting current user)
	 * @param <uri> : parties matching uri
	 */
	// TODO : REMOVE trentK
	public SigmaEstablisher(String token, HashMap<ElGamalKey, String> uri, ElGamalKey t){
		trentK = t;
		
		// Matching the uris
		uris = uri;
		
		// Setup the signer
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = new UserSyncManagerImpl();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		signer = new SigmaSigner();
		signer.setKey(currentUser.getKey());
	}
	
	/**
	 * @param <c> : contract to be signed
	 */
	public void initialize(SigmaContract c){
		contract = c;
		
		/*
		 *Setup the field 
		 */
		contractId = new String(contract.getHashableData());
		contractClauses = new String(contract.getClauses().getHashableData());
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
	private void chooseTrent(){
		// TODO : choose trent and associate its uri in uris
		contract.setTrentKey(trentK);
		// Put a listener on Trent in case something goes wrong
		establisherService.addListener(new ServiceListener(){
			@Override
			public void notify(Messages messages){
				// TODO : deal with Trent messages (cf Trent class)
			}
		}, senPubK + "TRENT");
		
		sign();
	}
	
	
	
	/**
	 * The contract signing protocol
	 * TODO : Setup a timer for Trent
	 */
	private void sign(){
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
					while (round<=(N+1) && claimFormed){
						sendRound(++round);
					}
				}
			}
		}, senPubK.toString());

		// Send the first round
		sendRound(1);
	}
	

	
	/**
	 * Function to call if something goes wrong.
	 * It send Trent 4 informations : 
	 *		the round
	 * 		the uris of the parties
	 * 		the contract to be signed
	 * 		the encrypted (for Trent) signed claim 
	 */
	public void resolve(){		
		String[] content = new String[4];
		
		// Round
		content[0] = String.valueOf(round);
		
		// Uris
		JsonTools<HashMap<ElGamalKey, String>> json1 = new JsonTools<>(new TypeReference<HashMap<ElGamalKey, String>>(){});
		content[1] = json1.toJson(uris,false);
		
		// Contract
		JsonTools<SigmaContract> json2 = new JsonTools<>(new TypeReference<SigmaContract>(){});
		content[2] = json2.toJson(contract,false);
		
		// Claim(k)
		Signer<SigmaSignature,ElGamalKey> sig = SignerFactory.createSigmaSigner(); 
		sig.setKey(signer.getKey());
		SigmaSignature sigClaimK;
		if (round==0){
			sigClaimK = sig.sign("ABORT".getBytes());
		}else {
			JsonTools<Or[]> json = new JsonTools<>(new TypeReference<Or[]>(){});
			String claimK = json.toJson(promRoundSender[round], true);
			sigClaimK = sig.sign(claimK.getBytes());
		}
		JsonTools<SigmaSignature> json3 = new JsonTools<>(new TypeReference<SigmaSignature>(){});
		content[3] = encryptMsg(json3.toJson(sigClaimK, false), trentK);
		
		// Concatenate the content
		JsonTools<String[]> json = new JsonTools<>(new TypeReference<String[]>(){});
		String fullContent = json.toJson(content, false);
		
		establisherService.sendContract(contractId,
							trentK.getPublicKey().toString()+"TRENT",
							senPubK.toString(),
							fullContent,
							uris.get(trentK));
	}
	
	
	/**
	 * Trent function when there is a problem 
	 * TODO : create it !
	 */
	public void resolveTrent(){
	}
	
	
	/*
	 * Send the needed message to do the protocol, called in sign() method
	 * @param round : round we are at
	 * @param uris : the destination peers uris
	 */
	private void sendRound(int round){
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
				Or p = pcs.getPcs((contractClauses+(round)).getBytes(), keys.get(k), true);
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
	private void verifyAndStoreSignature(String message, String pubK){
		
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
				SigmaSignature signature;
				signature = json.toEntity(content, true);
				ElGamalKey ke = new ElGamalKey();
				ke.setPublicKey(senPubK);
				SigmaSigner s = new SigmaSigner();
				s.setKey(ke);
				s.setTrentK(trentK);
				s.setReceiverK(senderKey);
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
				if (getPrivateCS(msg).Verifies((contractClauses + k).getBytes())){
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
	private String getJson(Or pcs){
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		return json.toJson(pcs, true);
	}
	// Return the PCS (Or Object) from json
	private Or getPrivateCS(String pcs){
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		return json.toEntity(pcs, true);
	}
	// Return the message encrypted with public key
	private String encryptMsg(String msg, ElGamalKey key){
		Encrypter<ElGamalKey> encrypter = EncrypterFactory.createElGamalSerpentEncrypter();
		encrypter.setKey(key);
		return new String(encrypter.encrypt(msg.getBytes()));
	}
	// Return the message decrypted with private key 
	private String decryptMsg(String msg, ElGamalKey key){
		Encrypter<ElGamalKey> encrypter = EncrypterFactory.createElGamalSerpentEncrypter();
		encrypter.setKey(key);
		return new String(encrypter.decrypt(msg.getBytes()));
	}
}
