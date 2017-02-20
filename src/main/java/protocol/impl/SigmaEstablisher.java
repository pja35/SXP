package protocol.impl;


import com.fasterxml.jackson.core.type.TypeReference;

import controller.Application;
import controller.tools.JsonTools;
import crypt.api.signatures.Signable;
import model.entity.ElGamalKey;
import network.api.EstablisherService;
import network.api.Messages;
import network.api.ServiceListener;
import protocol.api.Establisher;
import protocol.api.Status;
import protocol.impl.sigma.Sender;
import protocol.impl.sigma.ElGamal;
import protocol.impl.sigma.Or;
import protocol.impl.sigma.PCSFabric;


/**
 * 
 * @author Nathanaël EON
 *
 *	Implements the sigma protocol
 */

public class SigmaEstablisher implements Establisher{
	
	/**
	 * status : ongoing state of signature
	 * contract : contract which is being signed
	 * Sender, Receiver, Trent : instances necessary to the signature
	 */
	private Status status = Status.NOWHERE;
	private String contract;
	private Sender sender;
	private ElGamalKey receiverK;
	private ElGamalKey trentK;
	private final EstablisherService establisher =(EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);
	private Or[] pcs = new Or[5];
	private int round = 0;

	
	
	//Getters
	public String getContract(){
		return contract;
	}
	public Status getStatus(){
		return status;
	}

	
	//Initialization (on clicking sign button)
	public void initialize(String c, String receiverUri){
		contract = c;
		establisher.sendContract("start", sender.getKeys().getPublicKey().toString(), contract, receiverUri);
		sign(receiverUri);
	}
	
	
	/**
	 * Constructor
	 * @param sen : Sender
	 * @param receK : Receiver Keys
	 * @param trenK : Trent Keys
	 */
	public SigmaEstablisher(Sender sen, ElGamalKey receK, ElGamalKey trenK, String msg){
		receiverK = receK;
		trentK = trenK;
		sender = sen;
		contract = msg;
		
		
		// A listener to launch the signature process if the other already started it 
		establisher.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				if (messages.getMessage("title").equals("start") && messages.getMessage("promI").equals(contract)){
					sign(messages.getMessage("source"));
				}
			}
		}, receiverK.getPublicKey().toString());
	}
	
	
	
	/**
	 * Called to realize the signing protocol, users already know each other
	 */
	public void sign(String receiverUri){
		establisher.removeListener(receiverUri);
		status = Status.SIGNING;
		final PCSFabric pcsf = new PCSFabric(sender, receiverK , trentK);
		
		// Listeners for other rounds, when we receive the k-1 response, we can send the round k
		// When we receive the 4th round, then we have the signature
		establisher.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				// Round of the message received
				int k = Integer.parseInt(messages.getMessage("title"));
				
				// We check the clear signature on the last round
				if (round>=4 && k==4 && isSigned(messages.getMessage("promI"))){
					contract = messages.getMessage("promI");
					pcs[4]=getPrivateCS(contract);
					status = Status.FINALIZED;
					System.out.println("\n----SIGNATURE FINALISÉE----\n Contrat signée : "+contract);
				}else if (k==4){
					resolve(4);
				}
				
				// If someone sent us the round+1 (or more) PCS, there is problem cause we didn't send the actual round
				else if(round<k){
					resolve(round);
				}
				
				// If we receive the message of round before we send one (before receiving the round -1) we shall store it for when needed
				else if(round==k && pcsf.PCSVerifies(getPrivateCS(messages.getMessage("promI")), (contract + k).getBytes())){
					pcs[k]=getPrivateCS(messages.getMessage("promI"));
				}
				
				// Otherwise, we just need to send the round
				else if(round==(k+1) && pcsf.PCSVerifies(getPrivateCS(messages.getMessage("promI")), (contract + k).getBytes())){
					pcs[k]=getPrivateCS(messages.getMessage("promI"));
					establisher.sendContract(String.valueOf(round), 
										sender.getKeys().getPublicKey().toString(),
										getJson(pcsf.createPcs((contract+(round)).getBytes())),
										messages.getMessage("source"));
					
					try{
						Thread.sleep(1000);
					}catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (pcs[round]!= null){
						establisher.sendContract(String.valueOf(++round), 
								sender.getKeys().getPublicKey().toString(),
								getJson(pcsf.createPcs((contract+(round)).getBytes())),
								messages.getMessage("source"));
					}
					round++;
				}
			}
			// Check the final message to be sure it has the signature on it
			private boolean isSigned(String s){
				//TODO make sure s contains the clear signature
				return true;
			}
		}, receiverK.getPublicKey().toString());

		// Send the first round
		round=2;
		pcs[0]=pcsf.createPcs((contract+1).getBytes());
		establisher.sendContract("1", 
				sender.getKeys().getPublicKey().toString(),
				getJson(pcsf.createPcs((contract+1).getBytes())),
				receiverUri);
	}
	
	
	private Signable<?> s;
	//Resolve in case of error
	public Signable<?> resolve(int k){
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
