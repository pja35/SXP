package protocol.impl.sigma.steps;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import crypt.factories.SignerFactory;
import crypt.impl.signatures.ElGamalSignature;
import crypt.impl.signatures.ElGamalSigner;
import model.entity.ElGamalKey;
import network.api.EstablisherService;
import network.api.EstablisherServiceListener;
import network.api.Peer;
import protocol.impl.SigmaEstablisher;
import protocol.impl.sigma.SigmaContract;

public class ProtocolStart implements ProtocolStep{
	
	public final static String TITLE = "START";
	
	@XmlElement(name="key")
	private ElGamalKey key;
	
	// Keeps track of who is ready
	@XmlElement(name="received")
	private String[] received;

	private SigmaEstablisher sigmaE;
	private ElGamalSigner signer;
	private Peer peer;
	private EstablisherService es;
	private SigmaContract contract;
	private HashMap<ElGamalKey,String> uris;
	
	
	@JsonCreator
	public ProtocolStart(@JsonProperty("key") ElGamalKey key,
			@JsonProperty("received") String[] received){
		this.key = key;
		this.signer = SignerFactory.createElGamalSigner();
		this.signer.setKey(key);
		this.received = received;
	}
	
	
	public ProtocolStart(SigmaEstablisher sigmaE,
			ElGamalKey key){
		this.sigmaE = sigmaE;
		this.peer = sigmaE.peer;
		this.es = sigmaE.establisherService;
		this.uris = sigmaE.sigmaEstablisherData.getUris();
		this.contract = sigmaE.sigmaEstablisherData.getContract();
		
		signer = SignerFactory.createElGamalSigner();
		signer.setKey(key);
		this.key = key;
		this.received = new String[contract.getParties().size()];
		
		this.setupListener();
	}
	
	@Override
	public void restore(SigmaEstablisher sigmaE){
		this.sigmaE = sigmaE;
		this.peer = sigmaE.peer;
		this.es = sigmaE.establisherService;
		this.uris = sigmaE.sigmaEstablisherData.getUris();
		this.contract = sigmaE.sigmaEstablisherData.getContract();
		
		this.setupListener();
	}

	
	@Override
	public String getName(){
		return TITLE;
	}
	
	@Override
	public int getRound(){
		return -1;
	}
	
	@Override
	public void sendMessage(){
		String title = TITLE + new String(contract.getHashableData());
		ElGamalSignature signature = signer.sign((title).getBytes());
		JsonTools<ElGamalSignature> json = new JsonTools<>(new TypeReference<ElGamalSignature>(){});
		es.sendContract(title, 
				json.toJson(signature), 
				signer.getKey().getPublicKey().toString(),
				peer,
				uris);
	}
	
	@Override
	public void setupListener(){
		final String contractId = new String(contract.getHashableData());
		
		es.removeListener(TITLE+contractId+signer.getKey().getPublicKey().toString());
		es.setListener("title",
				TITLE+contractId, 
				TITLE+contractId+signer.getKey().getPublicKey().toString(),
				new EstablisherServiceListener(){
			@Override
			public void notify(String title, String content, String senderId) {
		
				// Check on title is done before
				JsonTools<ElGamalSignature> json = new JsonTools<>(new TypeReference<ElGamalSignature>(){});
				ElGamalSignature signature = json.toEntity(content);
				BigInteger msgSenKey = new BigInteger(senderId);
				// Get the sender index
				int i = 0;
				while (!(contract.getParties().get(i).getPublicKey().equals(msgSenKey))){i++;}
				
				// Prepare the elGamalSigner to check the data
				ElGamalSigner elGamalSigner = SignerFactory.createElGamalSigner();
				elGamalSigner.setKey(contract.getParties().get(i));
				
				if (elGamalSigner.verify((TITLE+contractId).getBytes(), signature)){
					received[i] = "";
					// Checks if everyone is ready
					if (Arrays.asList(received).indexOf(null) == (-1)){
						sigmaE.chooseTrent();
						es.removeListener(TITLE+contractId+signer.getKey().getPublicKey().toString());
					}
				}
			}
		}, uris != null);
	}
	
	@Override
	public void stop(){
		String contractId = new String(contract.getHashableData());
		String senPubK = signer.getKey().getPublicKey().toString();
		es.removeListener(TITLE+contractId+senPubK);
	}
	
}
