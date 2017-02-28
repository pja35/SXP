package protocol.impl.sigma;


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
import crypt.impl.signatures.ElGamalSignature;
import crypt.impl.signatures.ElGamalSigner;
import model.entity.ElGamalKey;
import network.api.EstablisherService;
import network.api.Messages;
import network.api.ServiceListener;
import protocol.api.EstablisherListener;
import protocol.api.Status;
import protocol.impl.contract.ElGamalContract;


/**
 * 
 * @author Nathanaël EON
 *
 *	Implements the sigma protocol
 *		It will sign an unsigned contract
 */

public class SigmaEstablisher{
	
	public ArrayList<EstablisherListener> listeners = new ArrayList<EstablisherListener>();
	public Status status = Status.NOWHERE;
	
	private ElGamalContract contract;
	private ElGamalSigner signer = new ElGamalSigner();
	private HashMap<ElGamalKey, String> uris;
	private ElGamalKey trentK;
	
	private String contractId;
	private ArrayList<ElGamalKey> keys;
	private BigInteger senPubK;
	private int N;
	private final EstablisherService establisherService =(EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);
	private String[] ready;
	private Or[][] pcs;
	private int round = 0;
	
	
	
	/**
	 * Constructor
	 * 		Setup the signing protocol
	 * @param c : contract to be signed
	 * @param senderK
	 * @param uri : parties matching uri
	 */
	public SigmaEstablisher(ElGamalContract c, ElGamalKey senderK, 
						HashMap<ElGamalKey, String> uri){
		contract = c;
		signer.setKey(senderK);
		uris = uri;
		
		setup();
	}
	
	
	/**
	 * Setup the fields
	 * Setup the starting protocol listener 
	 */
	private void setup(){
		// A hash of the clauses : should be unique (same clauses = same contract)
		// TODO : check if "contract.getHashableData()" could work
		contractId = new String(contract.getClauses().getHashableData());
		// The El Gamal keys of all parties 
		keys = contract.getParties();
		senPubK = signer.getKey().getPublicKey();
		N = keys.size();
		// Used to know wether everyone has yet accepted the contract or not
		ready = new String[N];
		// Store the PCS received
		pcs = new Or[N+2][N];
		
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
				
				if (messages.getMessage("contract").equals("start") &&
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
											"start",
											uris.get(key));
		}
	}
	

	/**
	 * Choose Trent, put a listener for him, then start signing
	 */
	private void chooseTrent(){
		// TODO : choose trent and associate its uri in uris
		trentK = ElGamalAsymKeyFactory.create(false);
		
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
	 */
	private void sign(){
		setStatus(Status.SIGNING);
		
		// Necessary tools to create the PCS
		final PCSFabric[] pcsf = new PCSFabric[N];
		Sender sender = new Sender(signer.getKey());
		for (int k=0; k<N; k++){
			pcsf[k] = new PCSFabric(sender, keys.get(k), trentK);
		}
		round = 1;

		establisherService.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				if (messages.getMessage("title").equals(contractId)){
					String msg = messages.getMessage("contract");
					
					// Checks if the message is a PCS, if yes store it in "pcs[round][k]"
					verifyAndStoreSignature(msg, messages.getMessage("sourceId") ,round, pcsf);
					
					/*
					 *  Send the rounds (if we have the claim needed):
					 *  	We do a loop because sometimes, we receive the PCS for round+1 before the one for the current round
					 *  
					 */  
					
					while (round<=(N+1) && claimFormed()){
						sendRound(++round, pcsf);
					}
				}
			}
		}, senPubK.toString());

		// Send the first round
		sendRound(1, pcsf);
	}
	
	/**
	 * Check if the current round is completely received
	 * @return
	 * 		true : round ok, can send the next one
	 * 		false : round not complete, keep waiting
	 */
	private boolean claimFormed(){
		for (int k=0; k<N; k++)
			if (pcs[round][k] == null)
				return false;
		return true;
	}	
	/**
	 * Send the needed message to do the protocol
	 * @param round : round we are at
	 * @param uris : the destination peers uris
	 */
	private void sendRound(int round, PCSFabric[] pcsfs){
		// Loop : send to every party the correct message
		for (int k=0; k<N; k++){
			
			// Public key of the receiver
			ElGamalKey key = keys.get(k);
			
			// If the receiver is the sender, isSender = true 
			boolean isSender = key.getPublicKey().equals(senPubK);
			
			// Content of the message which will be sent
			String content;
			
			// On the last round, send the clear signature
			if (round==(N+2)){
				JsonTools<ElGamalSignature> json = new JsonTools<>(new TypeReference<ElGamalSignature>(){});
				ElGamalSignature signature = pcsfs[k].getClearSignature(contract);
				if (isSender) 
					contract.addSignature(keys.get(k), signature);
				content = json.toJson(signature);
			
			// Otherwise send round k
			}else {
				Or p = pcsfs[k].createPcs((contractId+(round)).getBytes());
				content=encryptMsg(getJson(p), key);
				if (isSender){
					pcs[round][k] = p;
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
									key.getPublicKey().toString(),
									senPubK.toString(),
									fullContentS,
									uris.get(key));
			}
		}
	}
	/**
	 * Verify the message received (if the message is the last, check if the signature is ok)
	 * @param message : message we receive (messages.getMessage("contract"))
	 * @param contract : the contract we want to be signed in the end
	 * @param round : the round we are at
	 * @return
	 */
	private void verifyAndStoreSignature(String message, String pubK ,int round, PCSFabric[] pcsfs){
		
		// Get the keys of the sender of the message
		BigInteger msgSenKey = new BigInteger(pubK);
		int i = 0;
		while (!(keys.get(i).getPublicKey().equals(msgSenKey))){i++;}
		ElGamalKey key = keys.get(i);
		
		// From json message to the object {"k", PCS}
		JsonTools<String[]> json0 = new JsonTools<>(new TypeReference<String[]>(){});
		String[] fullContent = json0.toEntity(message);
		
		int k= Integer.parseInt(fullContent[0]);
		String content = fullContent[1];
		
		// Don't do anything if the sender is the actual user (shouldn't happen though)
		if (!(key.getPublicKey().equals(senPubK))){
			// If it's the last round, test the clear signature
			if (k == (N+2)){
				JsonTools<ElGamalSignature> json = new JsonTools<>(new TypeReference<ElGamalSignature>(){});
				ElGamalSignature signature;
				signature = json.toEntity(content, true);
				if (pcsfs[i].verifySignature(signature, contract, keys.get(i))) {
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
				if (pcsfs[i].PCSVerifies(getPrivateCS(msg),(contractId + k).getBytes())) {
					pcs[k][i]=getPrivateCS(msg);
				}
			}
		}
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
		JsonTools<ElGamalContract> json2 = new JsonTools<>(new TypeReference<ElGamalContract>(){});
		content[2] = json2.toJson(contract,false);
		
		// Claim(k)
		Signer<ElGamalSignature,ElGamalKey> sig = SignerFactory.createElGamalSigner(); 
		sig.setKey(signer.getKey());
		ElGamalSignature sigClaimK;
		if (round==0){
			sigClaimK = sig.sign("ABORT".getBytes());
		}else {
			JsonTools<Or[]> json = new JsonTools<>(new TypeReference<Or[]>(){});
			String claimK = json.toJson(pcs[round], true);
			sigClaimK = sig.sign(claimK.getBytes());
		}
		JsonTools<ElGamalSignature> json3 = new JsonTools<>(new TypeReference<ElGamalSignature>(){});
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

	
	//What Trent got to do
	public void resolveTrent(){
		
	}
	

	
	
	
	public ElGamalContract getContract(){
		return contract;
	}
	
	private void setStatus(Status s){
		status = s;
		notifyListeners();
	}

	public void addListener(EstablisherListener l) {
		listeners.add(l);
	}
	
	public void notifyListeners() {
		for (EstablisherListener l : listeners){
			l.establisherEvent(this.status);
		}
	}
	
	
	
	/*
	 * What follows are the necessary primitives for the signature to be done over the network
	 * 		Transformation json <-> object
	 * 		Decrypting <-> Encrypting
	 * 		Password creation
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
	// Return the encrypted message
	private String encryptMsg(String msg, ElGamalKey key){
		Encrypter<ElGamalKey> encrypter = EncrypterFactory.createElGamalSerpentEncrypter();
		encrypter.setKey(key);
		return new String(encrypter.encrypt(msg.getBytes()));
	}
	// Return the decrypted message
	private String decryptMsg(String msg, ElGamalKey key){
		Encrypter<ElGamalKey> encrypter = EncrypterFactory.createElGamalSerpentEncrypter();
		encrypter.setKey(key);
		return new String(encrypter.decrypt(msg.getBytes()));
	}
}
