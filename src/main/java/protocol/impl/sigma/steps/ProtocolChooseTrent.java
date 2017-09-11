package protocol.impl.sigma.steps;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ListIterator;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import controller.Users;
import controller.tools.JsonTools;
import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;
import model.entity.User;
import network.api.EstablisherService;
import network.api.EstablisherServiceListener;
import network.api.Peer;
import protocol.impl.SigmaEstablisher;
import protocol.impl.sigma.SigmaContract;

/**
 * Choose Trent with the other peers for this contract
 * @author neon@ec-m.fr
 * 
 * The format of data sent here is a String[2] with
 * 		data[0] = round
 * 		data[1] = jsonSent
 *
 *	First round - setup a list of potential TTP
 *	Second round - choose a random TTP
 *	Third round - checks that everyone has same TTP
 */
public class ProtocolChooseTrent implements ProtocolStep {
	
	public static final String TITLE  = "CHOOSING_TRENT";

	@XmlElement(name="list")
	final private ArrayList<User> list;
	
	@XmlElement(name="randomNumber")
	private BigInteger randomNumber;
	@XmlElement(name="finalNumber")
	private BigInteger finalNumber;
	
	@XmlElement(name="hasSent")
	private String[][] hasSent = new String[3][];

	@XmlElement(name="key")
	private ElGamalKey key;
	
	
	private SigmaEstablisher sigmaE;
	private Peer peer;
	private HashMap<ElGamalKey,String> uris;
	private EstablisherService es;
	private SigmaContract contract;
	private int senderKeyId;

	final private JsonTools<Collection<User>> json = new JsonTools<>(new TypeReference<Collection<User>>(){});
	final private JsonTools<String[]> jsonMessage = new JsonTools<>(new TypeReference<String[]>(){});
	
	/**
	 * Used when the protocol stopped and need to be restarted from scratch where it stopped
	 */
	@JsonCreator
	public ProtocolChooseTrent(@JsonProperty("list") ArrayList<User> list,
			@JsonProperty("randomNumber") BigInteger randomNumber,
			@JsonProperty("finalNumber") BigInteger finalNumber,
			@JsonProperty("hasSent") String[][] hasSent,
			@JsonProperty("key") ElGamalKey key){
		this.list = list;
		this.randomNumber = randomNumber;
		this.finalNumber = finalNumber;
		this.hasSent = hasSent;
		this.key = key;
		
		this.senderKeyId = 0;
		String senPubK = key.getPublicKey().toString();
		while (!(contract.getParties().get(this.senderKeyId).getPublicKey().toString().equals(senPubK))){this.senderKeyId++;}
	}
	
	/**
	 * Constructor for the step
	 * @param sigmaE : the current sigmaEstablisher it is started from
	 * @param key : signer key
	 */
	public ProtocolChooseTrent(SigmaEstablisher sigmaE,
			ElGamalKey key){
		
		this.key = key;
		this.sigmaE = sigmaE;
		this.peer = sigmaE.peer;
		this.uris = sigmaE.sigmaEstablisherData.getUris();
		this.es = sigmaE.establisherService;
		this.contract = sigmaE.sigmaEstablisherData.getContract();
	
		// Setup list of users (remove the signers)
		this.list = new ArrayList<User>(json.toEntity((new Users()).get()));
		for (ElGamalKey k : contract.getParties()){
	        ListIterator<User> it = list.listIterator();  
			while(it.hasNext())
				if (k.getPublicKey().equals(it.next().getKey().getPublicKey()))
					it.remove();
		}
		
		// Setup the random number which will be sent
		this.randomNumber = new BigInteger(100, new SecureRandom());
		this.finalNumber = this.randomNumber;
		
		int i=0;
		String senPubK = key.getPublicKey().toString();
		while (!(contract.getParties().get(i).getPublicKey().toString().equals(senPubK))){i++;}
		for (int k=0; k<hasSent.length; k++)
			hasSent[k] = new String[contract.getParties().size() + 1];
		this.senderKeyId = i;
		
		// Setup the listener on other peers
		this.setupListener();
	}
	
	@Override
	/**
	 * Called to start again
	 */
	public void restore(SigmaEstablisher sigmaE){
		this.sigmaE = sigmaE;
		this.peer = sigmaE.peer;
		this.uris = sigmaE.sigmaEstablisherData.getUris();
		this.es = sigmaE.establisherService;
		this.contract = sigmaE.sigmaEstablisherData.getContract();
		
		this.setupListener();
	}
	
	
	@Override
	public String getName() {
		return TITLE;
	}

	
	@Override
	/*
	 * The round here is 
	 * 		+ 0 if the list hasn't been setup with other peers
	 * 		+ 1 if the random numbers aren't all recovered
	 * 		+ 2 if Trent is already chosen
	 */
	public int getRound() {
		if (Arrays.asList(hasSent[0]).indexOf(null) != (-1))
			return 0;
		else if (Arrays.asList(hasSent[1]).indexOf(null) != (-1))
			return 1;
		return 2;
	}

	
	@Override
	public void sendMessage() {
		String[] content = {"0", json.toJson(list)};
		String senPubK = key.getPublicKey().toString();
		
		es.sendContract(TITLE+new String(contract.getHashableData()),
				jsonMessage.toJson(content), 
				senPubK,
				peer, 
				uris);
		hasSent[0][senderKeyId] = "";
	}

	
	@Override
	public void setupListener() {
		final String contractId = new String(contract.getHashableData());
		final String senPubK = key.getPublicKey().toString();
		final int N = contract.getParties().size();
		
		es.removeListener(TITLE+contractId+senPubK);
		es.setListener("title", TITLE+contractId, TITLE+contractId+senPubK, new EstablisherServiceListener() {
			@Override
			public void notify(String title, String msg, String senderId) {
				String[] content = jsonMessage.toEntity(msg);
				int j = 0;
				while (!(contract.getParties().get(j).getPublicKey().toString().equals(senderId))){j++;}
				// If we received a new list
				if (content[0].equals("0") && Arrays.asList(hasSent[0]).indexOf(null) != (-1)){
					Collection<User> list2 = json.toEntity(content[1]);
			        ListIterator<User> it = list.listIterator();  
					while(it.hasNext()){
						boolean isInBoth = false;
						for (User u : list2){
							if (u.getKey().getPublicKey().equals(it.next().getKey().getPublicKey()))
								isInBoth = true;
						}
						if (!isInBoth)
							it.remove();
					}
					hasSent[0][j] = "";
					
					if (Arrays.asList(hasSent[0]).indexOf(null) == N){
						hasSent[0][N] = "";
						list.sort(new Comparator<User>(){
							@Override
							public int compare(User u1, User u2){
								return u1.getKey().getPublicKey().compareTo(u2.getKey().getPublicKey());
							}
						});
						String[] toBeSent = new String[2];
						toBeSent[0] = "1";
						toBeSent[1] = randomNumber.toString();
						hasSent[1][senderKeyId] = "";
						es.sendContract(TITLE+contractId, jsonMessage.toJson(toBeSent), senPubK, peer, uris);
					}
				}
				// If we receive the others random number
				else if (content[0].equals("1") && Arrays.asList(hasSent[1]).indexOf(null) != (-1)){
					// Wait for everyone to have sent their number and setup Trent
					if (hasSent[1][j] == null){
						finalNumber = finalNumber.add(new BigInteger(content[1]));
						hasSent[1][j] = "";
					}
						
					if (Arrays.asList(hasSent[1]).indexOf(null) == N){
						hasSent[1][N] = "";
						int N2 = (int) list.size();
						if (N2 == 0){
							System.out.println("Can't go on - there is no third party available");
						}else{
							User trentUser = list.get(finalNumber.mod(new BigInteger(String.valueOf(N2))).intValue());
							if (sigmaE.sigmaEstablisherData.getTrentKey() ==null){
								sigmaE.setTrent(trentUser.getKey());
							}
							
							String[] toBeSent = new String[2];
							toBeSent[0] = "2";
							toBeSent[1] = trentUser.getKey().getPublicKey().toString();
							es.sendContract(TITLE+contractId, jsonMessage.toJson(toBeSent), senPubK, peer, uris);
							hasSent[2][senderKeyId] = "";
							
							if (sigmaE.sigmaEstablisherData.getTrentKey() !=null &&
									!sigmaE.sigmaEstablisherData.getTrentKey().getPublicKey().equals(trentUser.getKey().getPublicKey())){
								for (int k=0; k<hasSent.length; k++)
									hasSent[k] = new String[contract.getParties().size()];
								sigmaE.setTrent(null);
								sendMessage();
							}
						}
					}
				}
				// Check that we have the same Trent
				else if (content[0].equals("2") && Arrays.asList(hasSent[2]).indexOf(null) != (-1)){
					ElGamalKey key = sigmaE.sigmaEstablisherData.getTrentKey();
					if (key==null){
						ElGamalKey trentK = ElGamalAsymKeyFactory.create(false);
						trentK.setPublicKey(new BigInteger(content[1]));
						sigmaE.setTrent(trentK);
						hasSent[2][j] = "";
					}else if(content[1].equals(key.getPublicKey().toString())){
						hasSent[2][j] = "";
						if (Arrays.asList(hasSent[2]).indexOf(null) == (N)){
							hasSent[2][N] = ""; 
							nextStep();
						}
					}else {
						for (int k=0; k<hasSent.length; k++)
							hasSent[k] = new String[contract.getParties().size()];
						sigmaE.setTrent(null);
						sendMessage();
					}
				}
			}
		}, uris != null);

	}

	@Override
	public void stop() {
		String contractId = new String(contract.getHashableData());
		String senPubK = key.getPublicKey().toString();
		es.removeListener(TITLE+contractId+senPubK.toString());
	}
	
	/**
	 * Contains what needs to be done after this step
	 */
	private void nextStep(){
		sigmaE.setListenerOnTrent();
	}

}
