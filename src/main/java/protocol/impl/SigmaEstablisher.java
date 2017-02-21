package protocol.impl;


import com.fasterxml.jackson.core.type.TypeReference;

import controller.Application;
import controller.tools.JsonTools;
import crypt.api.signatures.Signable;
import crypt.api.signatures.Signer;
import crypt.factories.SignerFactory;
import crypt.impl.signatures.ElGamalSignature;
import model.entity.ElGamalKey;
import network.api.EstablisherService;
import network.api.Messages;
import network.api.ServiceListener;
import protocol.impl.sigma.Sender;
import protocol.impl.contract.ElGamalContract;
import protocol.impl.sigma.ElGamal;
import protocol.impl.sigma.Or;
import protocol.impl.sigma.PCSFabric;


/**
 * 
 * @author Nathanaël EON
 *
 *	Implements the sigma protocol
 *		For the messages sent, the who param of sendContract method is the sender public key
 */

public class SigmaEstablisher{
	
	
	/**
	 * contract : contract which is being signed
	 * contractSigned
	 * sender : instance necessary to the signature
	 * receiverK & trentK : keys necessary to the signature
	 * 
	 * status : ongoing state of signature
	 * establisherService : object to send messages
	 * pcs : store the received pcs (array for which each index matches a round in the protocol)
	 * round : keeping track of the rounds in the protocol 
	 */
	
	private String contract ;
	private String contractSigned;
	private Sender sender;
	private ElGamalKey receiverK;
	private ElGamalKey trentK;

	private final EstablisherService establisherService =(EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);
	private Or[] pcs = new Or[4];
	private int round = 0;

	

	public SigmaEstablisher(Sender sen, ElGamalKey receK, ElGamalKey trenK, String msg){
		receiverK = receK;
		trentK = trenK;
		sender = sen;
		contract = msg;
		
		// A listener to launch the signature process if the other
		// started initialize method with the correct contract
		establisherService.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				if (messages.getMessage("title").equals("start") &&
					messages.getMessage("contract").equals(contract)){
					sign(messages.getMessage("source"));
				}
			}
		}, sender.getKeys().getPublicKey().toString());
	}
	
	
	public void initialize(String c, String receiverUri){
		contract = c;
		establisherService.sendContract("start",
										receiverK.getPublicKey().toString(), 
										contract,
										receiverUri);
		sign(receiverUri);
	}
	
	
	public void sign(String receiverUri){
		
		final PCSFabric pcsf = new PCSFabric(sender, receiverK , trentK);
		round = 1;
		
		establisherService.removeListener(sender.getKeys().getPublicKey().toString());
		
		establisherService.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				int k = Integer.parseInt(messages.getMessage("title"));
				String msg = messages.getMessage("contract");
				
				boolean isVerified = verifySignature(msg, contract, round, pcsf);
				if (k<4 && isVerified){
					pcs[k]=getPrivateCS(msg);
				}

				while (round<4 && pcs[round] != null ){
					sendRound(++round, pcsf, messages.getMessage("source"));
				}
			}
		}, sender.getKeys().getPublicKey().toString());

		// Send the first round
		pcs[0]=pcsf.createPcs((contract+1).getBytes());
		establisherService.sendContract("1", 
				receiverK.getPublicKey().toString(),
				getJson(pcsf.createPcs((contract+1).getBytes())),
				receiverUri);
	}
	
	/**
	 * Send the needed message to do the protocol
	 * @param round : round we are at
	 * @param uris : the destination peers uris
	 */
	private void sendRound(int round, PCSFabric pcsf, String... uris){
		String content;
		if (round==4){
			JsonTools<ElGamalSignature> json = new JsonTools<>(new TypeReference<ElGamalSignature>(){});
			content = json.toJson(pcsf.getClearSignature(contract));
		}else {
			content = getJson(pcsf.createPcs((contract+(round)).getBytes()));
		}
		
		establisherService.sendContract(String.valueOf(round), 
							receiverK.getPublicKey().toString(),
							content,
							uris);
	}
	
	/**
	 * Verify the message send (if the message is the last, check if the signature is ok)
	 * @param message : message we receive (messages.getMessage("contract"))
	 * @param contract : the contract we want to be signed in the end
	 * @param round : the round we are at
	 * @return
	 */
	private boolean verifySignature(String message, String contract, int round, PCSFabric pcsf){
		if (round == 4){
			JsonTools<ElGamalSignature> json = new JsonTools<>(new TypeReference<ElGamalSignature>(){});
			ElGamalSignature signature = json.toEntity(message, true);
			System.out.println("\n----SIGNATURE FINALIZED----\n Contract signed : "+contract);
			if (pcsf.verifySignature(signature, contract)) {
				contractSigned = message;
				return true;
			}
			return false;
		}else {
			return pcsf.PCSVerifies(getPrivateCS(message), (contract + round).getBytes());
		}
	}
	
	
	
	
	
	private Signable<?> s;
	//Resolve in case of error
	public Signable<?> resolve(int k){
		Signer<ElGamalSignature,ElGamalKey> sig = SignerFactory.createElGamalSigner(); 
		sig.setKey(sender.getKeys());
		ElGamalSignature sigClaimK = sig.sign(getJson(pcs[k]).getBytes());
		
		
		
		return s;
	}


	//What Trent got to do
	public Signable<?> resolveTrent(){
		return s;
	}
	
	

	public String getSignedContract(){
		return contractSigned;
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
	//Return the encrypted string representing the private contract signature using receiver key
	public String getJson(Or pcs, boolean toEncrypt){
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		String msg = json.toJson(pcs, true);
		ElGamal eg = new ElGamal(receiverK);
		return new String(eg.encryptWithPublicKey(msg.getBytes()));
	}

	//Return the PCS (Or Object) from json
	public Or getPrivateCS(String pcs){
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		return json.toEntity(pcs, true);
	}
	//Return the PCS (Or Object) from json when it is encrypted with sender publicKey
	public Or getPrivateCS(String pcs, boolean encrypted){
		ElGamal eg = new ElGamal(sender.getKeys());
		byte[] msg = eg.decryptWithPrivateKey(pcs.getBytes());
		JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
		return json.toEntity(new String(msg), true);
	}
}
