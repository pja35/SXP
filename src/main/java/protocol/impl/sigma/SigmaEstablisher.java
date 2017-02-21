package protocol.impl.sigma;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.Application;
import controller.tools.JsonTools;
import crypt.api.signatures.Signable;
import crypt.api.signatures.Signer;
import crypt.factories.SignerFactory;
import crypt.impl.encryption.SerpentEncrypter;
import crypt.impl.signatures.ElGamalSignature;
import crypt.impl.signatures.ElGamalSigner;
import model.entity.ElGamalKey;
import network.api.EstablisherService;
import network.api.Messages;
import network.api.ServiceListener;
import protocol.impl.contract.ElGamalContract;


/**
 * 
 * @author Nathanaël EON
 *
 *	Implements the sigma protocol
 *		It will sign an unsigned contract
 */

public class SigmaEstablisher{
	
	private ElGamalContract contract;
	private ElGamalSigner signer = new ElGamalSigner();
	private ElGamalKey trentK;
	private String trentUri;
	
	private String clausesString;
	private ArrayList<ElGamalKey> keys;
	private HashMap<ElGamalKey, String> uris;
	private HashMap<ElGamalKey, String> passwords;
	private BigInteger senPubK;
	private int N;
	private final EstablisherService establisherService =(EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);
	private Or[][] pcs;
	private int round = 0;

	
	/**
	 * Constructor
	 * 		Note : we put a listener to know when protocol starts
	 * @param c : contract to be signed
	 * @param tK : trentKey
	 * @param senderK
	 * @param uri : parties matching uri
	 */
	public SigmaEstablisher(ElGamalContract c, ElGamalKey senderK, ElGamalKey tK, HashMap<ElGamalKey, String> uri, HashMap<ElGamalKey, String> psw){
		contract = c;
		signer.setKey(senderK);
		trentK = tK;
		uris = uri;
		passwords = psw;
		
		clausesString = new String(contract.getClauses().getHashableData());
		keys = contract.getParties();
		senPubK = senderK.getPublicKey();
		N = keys.size();
		pcs = new Or[N+2][N];
		
		establisherService.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				if (messages.getMessage("title").equals("start") &&
					(new String(messages.getMessage("contract"))).equals(new String(clausesString))){
					sign();
				}
			}
		}, senPubK.toString());
	}
	
	
	public void start(){
		for (int k=0; k<N; k++){
			ElGamalKey key = keys.get(k);
			establisherService.sendContract("start",
											key.getPublicKey().toString(), 
											clausesString,
											uris.get(key));
		}
	}
	
	
	public void sign(){
		
		final PCSFabric[] pcsf = new PCSFabric[N];
		Sender sender = new Sender(signer.getKey());
		for (int k=0; k<N; k++){
			pcsf[k] = new PCSFabric(sender, keys.get(k) , trentK);
		}
		round = 1;

		establisherService.removeListener(senPubK.toString());
		
		establisherService.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				int k = Integer.parseInt(messages.getMessage("title"));
				String msg = messages.getMessage("contract");
				
				verifyAndStoreSignature(msg, round, k, pcsf);

				while (round<=(N+1) && claimFormed()){
					sendRound(++round, pcsf);
				}
			}
		}, senPubK.toString());

		// Send the first round
		sendRound(1, pcsf);
	}
	
	private boolean claimFormed(){
		for (int k=0; k<N; k++){
			if (pcs[round][k] == null) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Send the needed message to do the protocol
	 * @param round : round we are at
	 * @param uris : the destination peers uris
	 */
	private void sendRound(int round, PCSFabric[] pcsfs){
		for (int k=0; k<N; k++){
			ElGamalKey key = keys.get(k);
			boolean isSender = key.getPublicKey().equals(senPubK);
			String content;
			if (round==(N+2)){
				JsonTools<ElGamalSignature> json = new JsonTools<>(new TypeReference<ElGamalSignature>(){});
				ElGamalSignature signature = pcsfs[k].getClearSignature(clausesString);
				if (isSender){
					contract.addSignature(keys.get(k), signature);
				}
				content = json.toJson(signature);
			}else {
				Or p = pcsfs[k].createPcs((clausesString+(round)).getBytes());
				content=encryptMsg(getJson(p), passwords.get(key));
				if (isSender){
					pcs[round][k] = p;
				}
			} 
			if (!isSender){
				System.out.println("SENDING ROUND : " + round);
				establisherService.sendContract(String.valueOf(round), 
									key.getPublicKey().toString(),
									content,
									uris.get(key));
			}
		}
	}
	
	/**
	 * Verify the message send (if the message is the last, check if the signature is ok)
	 * @param message : message we receive (messages.getMessage("contract"))
	 * @param contract : the contract we want to be signed in the end
	 * @param round : the round we are at
	 * @return
	 */
	private void verifyAndStoreSignature(String message, int round, int k, PCSFabric[] pcsfs){
		int i=0;
		while (i<N) {
			ElGamalKey key =keys.get(i);
			if (key.getPublicKey().equals(senPubK)){
				i++;
				continue;
			}else if (k == (N+2)){
				JsonTools<ElGamalSignature> json = new JsonTools<>(new TypeReference<ElGamalSignature>(){});
				ElGamalSignature signature;
				signature = json.toEntity(message, true);
				if (pcsfs[i].verifySignature(signature, clausesString)) {
					contract.addSignature(keys.get(i), signature);
					if (contract.isFinalized()){
						System.out.println("--- CONTRACT FINALIZED -- Key : " + senPubK);
					}
				}
			}else {
				String msg = decryptMsg(message, passwords.get(key));
				if (pcsfs[i].PCSVerifies(getPrivateCS(msg), (clausesString + round).getBytes())) {
					pcs[k][i]=getPrivateCS(msg);
				}
			}
			i++;
		}
	}
	
	
	
	
	
	private Signable<?> s;
	//Resolve in case of error
	public Signable<?> resolve(int k){
		Signer<ElGamalSignature,ElGamalKey> sig = SignerFactory.createElGamalSigner(); 
		sig.setKey(signer.getKey());
		ElGamalSignature sigClaimK;
		if (k==0){
			sigClaimK = sig.sign("ABORT".getBytes());
		}else {
			JsonTools<Or[]> json = new JsonTools<>(new TypeReference<Or[]>(){});
			String claimK = json.toJson(pcs[k], true);
			sigClaimK = sig.sign(claimK.getBytes());
		}
		JsonTools<ElGamalSignature> json = new JsonTools<>(new TypeReference<ElGamalSignature>(){});
		String encSigClaimK = encryptMsg(json.toJson(sigClaimK, false), passwords.get(trentK));
		
		establisherService.sendContract(String.valueOf(round), 
							keys.get(k).toString(),
							encSigClaimK,
							trentUri);
		return s;
	}


	//What Trent got to do
	public Signable<?> resolveTrent(){
		return s;
	}
	
	
	
	/**
	 * What follows are the necessary primitives for the signature to be done over the network
	 * @return
	 */	
	//Return the string representing the private contract signature
	public String getJson(Or pcs){
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		return json.toJson(pcs, true);
	}
	//Return the PCS (Or Object) from json
	public Or getPrivateCS(String pcs){
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		return json.toEntity(pcs, true);
	}
	
	//Return the encrypted string 
	public String encryptMsg(String msg, String pwd){
		SerpentEncrypter encrypter = new SerpentEncrypter();
		encrypter.setKey(pwd);
		byte[] cypher = encrypter.encrypt(msg.getBytes());
		JsonTools<byte[]> json = new JsonTools<>(new TypeReference<byte[]>(){});
		return json.toJson(cypher, true);
	}
	//Return the decrypted message from a serpent encryption
	public String decryptMsg(String msg, String pwd){
		JsonTools<byte[]> json = new JsonTools<>(new TypeReference<byte[]>(){});
		byte[] pcs = json.toEntity(msg, true);
		SerpentEncrypter encrypter = new SerpentEncrypter();
		encrypter.setKey(pwd);
		return new String(encrypter.decrypt(pcs));
	}
}
