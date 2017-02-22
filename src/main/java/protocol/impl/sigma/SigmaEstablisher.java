package protocol.impl.sigma;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.Application;
import controller.tools.JsonTools;
import crypt.api.encryption.Encrypter;
import crypt.api.signatures.Signable;
import crypt.api.signatures.Signer;
import crypt.factories.EncrypterFactory;
import crypt.factories.SignerFactory;
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
	
	private String clausesString;
	private ArrayList<ElGamalKey> keys;
	private HashMap<ElGamalKey, String> uris;
	private HashMap<ElGamalKey, String> recPasswords = new HashMap<ElGamalKey, String>();
	private HashMap<ElGamalKey, String> senPasswords = new HashMap<ElGamalKey, String>();
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
	public SigmaEstablisher(ElGamalContract c, ElGamalKey senderK, ElGamalKey tK, 
						HashMap<ElGamalKey, String> uri){
		contract = c;
		signer.setKey(senderK);
		trentK = tK;
		uris = uri;
		
		setup();
	}
	
	
	/**
	 * Setup the fields and passwords for data encryption
	 * Setup the starting protocol listener 
	 */
	public void setup(){
		clausesString = new String(contract.getClauses().getHashableData());
		keys = contract.getParties();
		senPubK = signer.getKey().getPublicKey();
		N = keys.size();
		pcs = new Or[N+2][N];
		
		chooseTrent();
		
		// Set the sending passwords
		establisherService.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				if ((new String(messages.getMessage("title"))).equals(clausesString+"setPassword")){
					// Finding the sender
					BigInteger msgSenKey = new BigInteger(messages.getMessage("sourceId"));
					int i = 0;
					while (!(keys.get(i).getPublicKey().equals(msgSenKey))){i++;}
					ElGamalKey key = keys.get(i);
					
					// Decrypting the message
					JsonTools<byte[]> json = new JsonTools<>(new TypeReference<byte[]>(){});
					byte[] msg = json.toEntity(messages.getMessage("contract"));
					Encrypter<ElGamalKey> encrypter = EncrypterFactory.createElGamalEncrypter();
					encrypter.setKey(signer.getKey());
					byte[] content = encrypter.decrypt(msg);
					// Add the password
					senPasswords.put(key,  new String(content));
				}
				if (messages.getMessage("contract").equals("start") &&
					messages.getMessage("title").equals(clausesString)){
					sign();
				}
			}
		}, senPubK.toString());
		
		// Set the receiving passwords
		for (int i=0; i<N; i++){
			
			// Generating password
			String pwd = createPwd(20);
			
			Encrypter<ElGamalKey> encrypter = EncrypterFactory.createElGamalEncrypter();
			encrypter.setKey(keys.get(i));
			JsonTools<byte[]> json = new JsonTools<>(new TypeReference<byte[]>(){});
			String pwdEnc = json.toJson(encrypter.encrypt(pwd.getBytes()));
			//Adding to receiving passwords
			recPasswords.put(keys.get(i), pwd);
			//Send it to the other
			establisherService.sendContract(clausesString+"setPassword", 
					keys.get(i).getPublicKey().toString(),
					senPubK.toString(),
					pwdEnc,
					uris.get(keys.get(i)));
		}
	}
	
	
	/**
	 * Launch the protocol : used when everyone has agreed on a contract
	 */
	public void start(){
		for (int k=0; k<N; k++){
			ElGamalKey key = keys.get(k);
			establisherService.sendContract(clausesString,
											key.getPublicKey().toString(), 
											senPubK.toString(),
											"start",
											uris.get(key));
		}
	}
	
	/**
	 * The contract signing protocol
	 */
	public void sign(){
		
		// Necessary tools to create the PCS
		final PCSFabric[] pcsf = new PCSFabric[N];
		Sender sender = new Sender(signer.getKey());
		for (int k=0; k<N; k++){
			pcsf[k] = new PCSFabric(sender, keys.get(k) , trentK);
		}
		round = 1;

		establisherService.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				if (messages.getMessage("title").equals(clausesString)){
					String msg = messages.getMessage("contract");
					
					verifyAndStoreSignature(msg, messages.getMessage("sourceId") ,round, pcsf);
	
					while (round<=(N+1) && claimFormed()){
						sendRound(++round, pcsf);
					}
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
				content=encryptMsg(getJson(p), senPasswords.get(key));
				if (isSender){
					pcs[round][k] = p;
				}
			} 
			
			String[] fullContent = {String.valueOf(round), content};
			JsonTools<String[]> json2 = new JsonTools<>(new TypeReference<String[]>(){});
			String fullContentS = json2.toJson(fullContent);
			
			if (!isSender){
				System.out.println("SENDING ROUND : " + round);
				establisherService.sendContract(clausesString, 
									key.getPublicKey().toString(),
									senPubK.toString(),
									fullContentS,
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
	private void verifyAndStoreSignature(String message, String pubK ,int round, PCSFabric[] pcsfs){
		BigInteger msgSenKey = new BigInteger(pubK);
		
		int i = 0;
		while (!(keys.get(i).getPublicKey().equals(msgSenKey))){i++;}
		ElGamalKey key = keys.get(i);

		JsonTools<String[]> json0 = new JsonTools<>(new TypeReference<String[]>(){});
		String[] fullContent = json0.toEntity(message);
		
		int k= Integer.parseInt(fullContent[0]);
		String content = fullContent[1];
		
		if (!(key.getPublicKey().equals(senPubK))){
			if (k == (N+2)){
				JsonTools<ElGamalSignature> json = new JsonTools<>(new TypeReference<ElGamalSignature>(){});
				ElGamalSignature signature;
				signature = json.toEntity(content, true);
				if (pcsfs[i].verifySignature(signature, clausesString)) {
					contract.addSignature(keys.get(i), signature);
					if (contract.isFinalized()){
						System.out.println("--- CONTRACT FINALIZED -- Key : " + senPubK);
					}
				}
			}else {
				String msg = decryptMsg(content, recPasswords.get(key));
				if (pcsfs[i].PCSVerifies(getPrivateCS(msg), (clausesString + round).getBytes())) {
					pcs[k][i]=getPrivateCS(msg);
				}
			}
		i++;
		}
	}
	
	
	

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
		String encSigClaimK = encryptMsg(json.toJson(sigClaimK, false), senPasswords.get(trentK));
		
		establisherService.sendContract(String.valueOf(round), 
							keys.get(k).toString(),
							encSigClaimK,
							uris.get(trentK));
		return null;
	}

	
	
	private void chooseTrent(){
		// TODO : choose trent and associate its uri in uris
	}
	
	//What Trent got to do
	public Signable<?> resolveTrent(){
		return null;
	}
	
	
	
	/**
	 * What follows are the necessary primitives for the signature to be done over the network
	 * 		Transformation json <-> object
	 * 		Decrypting <-> Encrypting
	 * 		Password creation
	 *
	 */	
	// Return the string representing the private contract signature
	public String getJson(Or pcs){
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		return json.toJson(pcs, true);
	}
	// Return the PCS (Or Object) from json
	public Or getPrivateCS(String pcs){
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		return json.toEntity(pcs, true);
	}
	
	// Return the encrypted string with Serpent
	public String encryptMsg(String msg, String pwd){
		Encrypter<byte[]> encrypter = EncrypterFactory.createSerpentEncrypter();
		encrypter.setKey(pwd.getBytes());
		byte[] cypher = encrypter.encrypt(msg.getBytes());
		JsonTools<byte[]> json = new JsonTools<>(new TypeReference<byte[]>(){});
		return json.toJson(cypher, true);
	}
	// Return the decrypted message from a serpent encryption
	public String decryptMsg(String msg, String pwd){
		JsonTools<byte[]> json = new JsonTools<>(new TypeReference<byte[]>(){});
		byte[] pcs = json.toEntity(msg, true);
		Encrypter<byte[]> encrypter = EncrypterFactory.createSerpentEncrypter();
		encrypter.setKey(pwd.getBytes());
		return new String(encrypter.decrypt(pcs));
	}
	// Return a password 
	private String createPwd(int len){
		char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int j = 0; j < len; j++) {
		    char c = characters[random.nextInt(characters.length)];
		    sb.append(c);
		}
		return sb.toString();
	}
}
