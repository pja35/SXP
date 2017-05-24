package protocol.impl;


import java.math.BigInteger;
import java.util.HashMap;


import crypt.impl.signatures.SigmaSigner;

import controller.Application;
import crypt.factories.SignerFactory;
import model.api.Status;
import model.entity.ElGamalKey;
import model.entity.sigma.SigmaSignature;
import network.api.EstablisherService;
import network.api.Peer;
import protocol.api.Establisher;
import protocol.impl.sigma.SigmaContract;
import protocol.impl.sigma.SigmaEstablisherData;
import protocol.impl.sigma.steps.ProtocolResolve;
import protocol.impl.sigma.steps.ProtocolSign;
import protocol.impl.sigma.steps.ProtocolStart;
import protocol.impl.sigma.steps.ProtocolStep;


/** 
 *	Establisher for sigma protocol
 *
 * @author Nathanaël EON
 *
 * TODO : Change the messaging system to an asymetric one
 */

public class SigmaEstablisher extends Establisher<BigInteger, ElGamalKey, SigmaSignature, SigmaSigner, SigmaContract> {
	
	public static final String TRENT_CHOOSING_MESSAGE = "CHOOSE_TRENT";
	
	public SigmaEstablisherData sigmaEstablisherData;
	
	protected EstablisherService establisherService =(EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);
	protected Peer peer = Application.getInstance().getPeer();
	public ProtocolStep resolvingStep;
	
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
		
		this.sigmaEstablisherData = new SigmaEstablisherData();
		sigmaEstablisherData.setTrentKey(t);
		sigmaEstablisherData.setSenderKey(senderK);
	}
	
	/**
	 * @param <c> : contract to be signed
	 */
	@Override
	public void initialize(SigmaContract c){
		contract = c;
		/*
		 * Get ready to start
		 * If an advertisement was or is received, we check the signature and it is stored and we wait until everyone has sent its starter
		 */
		sigmaEstablisherData.setProtocolStep(new ProtocolStart(this, signer.getKey(), peer, uris, establisherService, contract));
		sigmaEstablisherData.getProtocolStep().setupListener();
	}
	
	/**
	 * Launch the protocol : tell everyone that the user is ready to sign (pressed signing button)
	 */
	@Override
	public void start(){
		sigmaEstablisherData.getProtocolStep().sendMessage();
	}

	/**
	 * Choose Trent, put a listener for him, then start signing
	 */
	public void chooseTrent(){
		sigmaEstablisherData.getProtocolStep().stop();
		/*
		 *Setup the field 
		 */
		
//		String listUsersJson = (new Users()).get();
//		
//		final JsonTools<Collection<User>> json = new JsonTools<>(new TypeReference<Collection<User>>(){});
//		final JsonTools<String[]> json2 = new JsonTools<>(new TypeReference<String[]>(){});
//		final Collection<User> listUsers = json.toEntity(listUsersJson);
//		final ArrayList<User> list = new ArrayList<User>(listUsers);
//		
//		String[] content = {"0", listUsersJson};
//		establisherService.sendContract(TRENT_CHOOSING_MESSAGE+contractId, json2.toJson(content), senPubK.toString(), peer, uris);
//		int i=0;
//		while (!(keys.get(i).getPublicKey().equals(senPubK))){i++;}
//		final String[] hasSent = new String[3*N];
//		hasSent[i] = "";
//		
//		final int senKeyId = i;
//
//		establisherService.removeListener(TRENT_CHOOSING_MESSAGE+contractId+senPubK.toString());
//		establisherService.setListener("title", TRENT_CHOOSING_MESSAGE+contractId, TRENT_CHOOSING_MESSAGE+contractId+senPubK.toString(), new EstablisherServiceListener() {
//			private BigInteger randomNumber = new BigInteger(100, new SecureRandom());
//			@Override
//			public void notify(String title, String msg, String senderId) {
//				String[] content = json2.toEntity(msg);
//				int j = 0;
//				while (!(keys.get(j).getPublicKey().toString().equals(senderId))){j++;}
//				System.out.println( content[0] + " ; " + senKeyId + " - " + j);
//				// If we received a new list
//				if (content[0].equals("0") && Arrays.asList(hasSent).indexOf(null) < N){
//					Collection<User> list2 = json.toEntity(content[1]);
//					listUsers.containsAll(list2);
//					hasSent[j] = "";
//					
//					if (Arrays.asList(hasSent).indexOf(null) == N){
//						Collections.sort(list, new Comparator<User>(){
//							@Override
//							public int compare(User u1, User u2){
//								return u1.getKey().getPublicKey().compareTo(u2.getKey().getPublicKey());
//							}
//						});
//						String[] toBeSent = new String[2];
//						toBeSent[0] = "1";
//						toBeSent[1] = randomNumber.toString();
//						establisherService.sendContract(TRENT_CHOOSING_MESSAGE+contractId, json2.toJson(toBeSent), senPubK.toString(), peer, uris);
//						hasSent[senKeyId+N] = "";
//					}
//				}
//				// If we receive the others random number
//				else if (content[0].equals("1") && Arrays.asList(hasSent).indexOf(null) < 2*N){
//					// Wait for everyone to have sent their number and setup Trent
//					if (hasSent[j+N] == null){
//						randomNumber = randomNumber.add(new BigInteger(content[1]));
//						hasSent[j+N] = "";
//						
//						if (Arrays.asList(hasSent).indexOf(null) == 2*N){
//							int N2 = (int) list.size();
//							User trentUser = list.get(randomNumber.mod(new BigInteger(String.valueOf(N2))).intValue());
//	
//							trentK = trentUser.getKey();
//							contract.setTrentKey(trentK);
//							signer.setTrentK(trentK);
//							
//							String[] toBeSent = new String[2];
//							toBeSent[0] = "2";
//							toBeSent[1] = trentK.getPublicKey().toString();
//							System.out.println(toBeSent[1]);
//							establisherService.sendContract(TRENT_CHOOSING_MESSAGE+contractId, json2.toJson(toBeSent), senPubK.toString(), peer, uris);
//							hasSent[senKeyId + 2*N] = "";
//						}
//					}
//				}
//				// Check that we have the same Trent
//				else if (content[0].equals("2") && Arrays.asList(hasSent).indexOf(null) != (-1)){
//					if (content[1].equals(trentK.getPublicKey().toString())){
//						hasSent[j+2*N] = "";
//						if (Arrays.asList(hasSent).indexOf(null) == (-1))
//							setListenerOnTrent();
//					}
//				}
//			}
//		}, uris != null);
		contract.setTrentKey(sigmaEstablisherData.getTrentKey());
		signer.setTrentK(sigmaEstablisherData.getTrentKey());
		
		
		setListenerOnTrent();
	}

	
	// Put a listener on Trent in case something goes wrong
	protected void setListenerOnTrent(){
		
		resolvingStep = new ProtocolResolve(this,
				establisherService,
				peer,
				contract,
				signer);
		resolvingStep.setupListener();
		
		if (getStatus() != Status.CANCELLED && getStatus() != Status.FINALIZED)
			sign();
	}
	
	/**
	 * The contract signing protocol
	 * TODO : Setup a timer that will trigger resolve()
	 */
	protected void sign(){
		sigmaEstablisherData.setProtocolStep(new ProtocolSign(this, this.establisherService, peer, uris, this.contract));
		sigmaEstablisherData.getProtocolStep().setupListener();
		sigmaEstablisherData.getProtocolStep().sendMessage();
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
		resolvingStep.sendMessage();
	}
}
