package protocol.impl.sigma.steps;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ListIterator;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.Users;
import controller.tools.JsonTools;
import model.entity.ElGamalKey;
import model.entity.User;
import network.api.EstablisherService;
import network.api.EstablisherServiceListener;
import network.api.Peer;
import protocol.impl.SigmaEstablisher;
import protocol.impl.sigma.SigmaContract;

public class ProtocolChooseTrent implements ProtocolStep {
	
	public static final String TITLE  = "CHOOSING_TRENT";

	private Peer peer;
	private HashMap<ElGamalKey,String> uris;
	private EstablisherService es;
	private SigmaContract contract;
	private SigmaEstablisher sigmaE;

	final private JsonTools<Collection<User>> json = new JsonTools<>(new TypeReference<Collection<User>>(){});
	final private JsonTools<String[]> jsonMessage = new JsonTools<>(new TypeReference<String[]>(){});
	final private ArrayList<User> list;
	
	private String[][] hasSent = new String[3][];
	private int senderKeyId;
	
	public ProtocolChooseTrent(SigmaEstablisher sigmaE,
			Peer peer, 
			HashMap<ElGamalKey,String> uris, 
			EstablisherService es,
			SigmaContract contract){

		this.peer = peer;
		this.uris = uris;
		this.es = es;
		this.contract = contract;
		this.sigmaE = sigmaE;
	
		// Setup list of users (remove the signers)
		this.list = new ArrayList<User>(json.toEntity((new Users()).get()));
		for (ElGamalKey key : contract.getParties()){
	        ListIterator<User> it = list.listIterator();  
			while(it.hasNext())
				if (key.getPublicKey().equals(it.next().getKey().getPublicKey()))
					it.remove();
			
		}
		
		
		
		int i=0;
		String senPubK = sigmaE.sigmaEstablisherData.getSenderKey().getPublicKey().toString();
		while (!(contract.getParties().get(i).getPublicKey().toString().equals(senPubK))){i++;}
		for (int k=0; k<hasSent.length; k++)
			hasSent[k] = new String[contract.getParties().size()];
		this.senderKeyId = i;
	}
	
	@Override
	public String getName() {
		return TITLE;
	}

	@Override
	public int getRound() {
		return 0;
	}

	@Override
	public void sendMessage() {

		String[] content = {"0", json.toJson(list)};
		String senPubK = sigmaE.sigmaEstablisherData.getSenderKey().getPublicKey().toString();
		
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
		final String senPubK = sigmaE.sigmaEstablisherData.getSenderKey().getPublicKey().toString();
		
		es.removeListener(TITLE+contractId+senPubK);
		es.setListener("title", TITLE+contractId, TITLE+contractId+senPubK, new EstablisherServiceListener() {
			private BigInteger randomNumber = new BigInteger(100, new SecureRandom());
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
					
					if (Arrays.asList(hasSent[0]).indexOf(null) == (-1)){
						list.sort(new Comparator<User>(){
							@Override
							public int compare(User u1, User u2){
								return u1.getKey().getPublicKey().compareTo(u2.getKey().getPublicKey());
							}
						});
						String[] toBeSent = new String[2];
						toBeSent[0] = "1";
						toBeSent[1] = randomNumber.toString();
						es.sendContract(TITLE+contractId, jsonMessage.toJson(toBeSent), senPubK, peer, uris);
						hasSent[1][senderKeyId] = "";
					}
				}
				// If we receive the others random number
				else if (content[0].equals("1") && Arrays.asList(hasSent[1]).indexOf(null) != (-1)){
					// Wait for everyone to have sent their number and setup Trent
					if (hasSent[1][j] == null){
						randomNumber = randomNumber.add(new BigInteger(content[1]));
						hasSent[1][j] = "";
						
						if (Arrays.asList(hasSent[1]).indexOf(null) == (-1)){
							int N2 = (int) list.size();
							if (N2 == 0){
								System.out.println("Can't go on - there is no third party available");
							}else{
								User trentUser = list.get(randomNumber.mod(new BigInteger(String.valueOf(N2))).intValue());
			
								sigmaE.setTrent(trentUser.getKey());
								
								String[] toBeSent = new String[2];
								toBeSent[0] = "2";
								toBeSent[1] = trentUser.getKey().getPublicKey().toString();
								es.sendContract(TITLE+contractId, jsonMessage.toJson(toBeSent), senPubK, peer, uris);
								hasSent[2][senderKeyId] = "";
							}
						}
					}
				}
				// Check that we have the same Trent
				else if (content[0].equals("2") && Arrays.asList(hasSent[2]).indexOf(null) != (-1)){
					ElGamalKey key = sigmaE.sigmaEstablisherData.getTrentKey();
					if (key==null || content[1].equals(key.getPublicKey().toString())){
						hasSent[2][j] = "";
						if (Arrays.asList(hasSent[2]).indexOf(null) == (-1))
							sigmaE.setListenerOnTrent();
					}
				}
			}
		}, uris != null);

	}

	@Override
	public void stop() {
		String contractId = new String(contract.getHashableData());
		String senPubK = sigmaE.sigmaEstablisherData.getSenderKey().getPublicKey().toString();
		es.removeListener(TITLE+contractId+senPubK.toString());
	}

}
