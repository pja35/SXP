package protocol.impl.sigma;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import org.bouncycastle.util.Arrays;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.Users;
import controller.tools.JsonTools;
import crypt.api.signatures.Signable;
import crypt.impl.signatures.SigmaSignature;
import crypt.impl.signatures.SigmaSigner;
import model.api.EstablisherType;
import model.api.Status;
import model.api.Wish;
import model.entity.ContractEntity;
import model.entity.ElGamalKey;
import model.entity.User;
import protocol.api.EstablisherContract;

/**
 * This class aims to create an adapter between ContractEntity and EstablisherContract. We want everything
 * to be in the ContractEntity and an establisher will use an EstablisherContract. That's why we use this
 * adapter in the establishers
 * 
 * @author Nathanaël Eon
 *
 */
public class SigmaContract extends EstablisherContract<BigInteger, ElGamalKey, SigmaSignature, SigmaSigner>{
	
	// List of parties keys
	protected ArrayList<ElGamalKey> parties = new ArrayList<>();
	// Maps the keys with the id of a user
	protected HashMap<ElGamalKey,String> partiesId = new HashMap<ElGamalKey, String>();
	// Maps the keys with the signatures
	protected HashMap<ElGamalKey, SigmaSignature> signatures = new HashMap<ElGamalKey, SigmaSignature>();
	// Clauses in the format we need them
	protected Signable<SigmaSignature> clauses = null;
	// Signer object
	protected SigmaSigner signer;
	
	// Basic constructor
	public SigmaContract(){
		super();
		this.signer = new SigmaSigner();
		this.contract = new ContractEntity();
		contract.setClauses(new ArrayList<String>());
		contract.setParties(new ArrayList<String>());
		contract.setSignatures(new HashMap<String,String>());
		contract.setEstablisherType(EstablisherType.Sigma);
	}
	
	// Constructor from clauses (problem when resolve, because no partiesId set)
	public SigmaContract(Signable<SigmaSignature> clauses){
		super();
		this.signer = new SigmaSigner();
		this.contract = new ContractEntity();
		this.setClauses(clauses);
		contract.setParties(new ArrayList<String>());
		contract.setSignatures(new HashMap<String,String>());
		contract.setEstablisherType(EstablisherType.Sigma);
	}
	
	// Constructor from a ContractEntity (what will be most used)
	public SigmaContract(ContractEntity c){
		super();
		this.contract=c;
		this.signer = new SigmaSigner();
		this.setClauses(contract.getClauses());
		this.setParties(contract.getParties());		
		this.contract.setEstablisherType(EstablisherType.Sigma);
	}

	/************* GETTERS ***********/
	public Signable<SigmaSignature> getClauses(){
		return clauses;
	}
	public ArrayList<ElGamalKey> getParties(){
		return parties;
	}
	public HashMap<String,String> getEstablishementData(){
		if (contract.getEstablishementData() == null){
			return new HashMap<String, String>();
		}			
		JsonTools<HashMap<String, String>> json = new JsonTools<>(new TypeReference<HashMap<String,String>>(){});
		return json.toEntity(contract.getEstablishementData());
	}
	public ElGamalKey getTrentKey(){
		String k = this.getEstablishementData().get("trentKey");
		if (k == null)
			return null;
		JsonTools<ElGamalKey> json = new JsonTools<>(new TypeReference<ElGamalKey>(){});
		return json.toEntity(k);
	}
	
	/************* SETTERS ***********/
	public void setClauses(ArrayList<String> c){
		this.clauses = new SigmaClauses(c);
		this.contract.setClauses(c);
	}
	public void setClauses(Signable<SigmaSignature> c){
		this.clauses = c;
		ArrayList<String> a = new ArrayList<String>();
		a.add(new String(c.getHashableData()));
		this.contract.setClauses(a);
	}
	
	/**
	 * Find the parties keys
	 * @param s : List of user ids
	 */
	public void setParties(ArrayList<String> s){
		for (String u : s){
			JsonTools<User> json = new JsonTools<>(new TypeReference<User>(){});
			Users users = new Users();
			User user = json.toEntity(users.get(u));
			this.parties.add(user.getKey());
			this.partiesId.put(user.getKey(), user.getId());
		}
		this.contract.setParties(s);
	}
	/**
	 * Set the parties from a list of ElGamalKey
	 * WARNING : this won't set the partiesId
	 */
	public void setParties(ArrayList<ElGamalKey> p, boolean isSigmaParty){
		this.parties = p;
	}
	
	/**
	 * Set the establishement data (with a name and a field)
	 */
	public void setEstablishementData(String name, String value){
		HashMap<String, String> estData = this.getEstablishementData();
		estData.put(name, value);
		JsonTools<HashMap<String, String>> json = new JsonTools<>(new TypeReference<HashMap<String,String>>(){});
		contract.setEstablishementData(json.toJson(estData));
	}

	/**
	 * Set Trent key and store it into Establishement data
	 */
	public void setTrentKey (ElGamalKey k){
		signer.setTrentK(k);
		JsonTools<ElGamalKey> json = new JsonTools<>(new TypeReference<ElGamalKey>(){});
		this.setEstablishementData("trentKey", json.toJson(k));
	}
	
	/************* STATUS / WISH ***********/
	public Status getStatus(){
		return contract.getStatus();
	}
	public void setStatus(Status s){
		contract.setStatus(s);
	}
	
	public Wish getWish(){
		return contract.getWish();
	}
	public void setWish(Wish w){
		contract.setWish(w);
	}
	
	/************* Abstract method implementation **********/
	
	@Override
	public boolean isFinalized() {
		if (this.getTrentKey() == null){
			return false;}
		for(ElGamalKey k: parties) {
			signer.setReceiverK(k);
			if(signatures.get(k) == null || !signer.verify(clauses.getHashableData(), signatures.get(k))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void addSignature(ElGamalKey k, SigmaSignature s) {
		if(k == null || !this.parties.contains(k)) {
			throw new RuntimeException("invalid key");
		}
		signatures.put(k, s);
		contract.getSignatures().put(this.partiesId.get(k), s.toString());
	}
	
	@Override
	public boolean checkContrat(EstablisherContract<BigInteger, ElGamalKey, SigmaSignature, SigmaSigner> contract) {
		return this.equals(contract) && this.isFinalized();
	}
	
	@Override
	public boolean equals(EstablisherContract<BigInteger, ElGamalKey, SigmaSignature, SigmaSigner> c) {
		if (!(c instanceof SigmaContract))
			return false;
		SigmaContract contract = (SigmaContract) c;
		if (contract.clauses == null)
			return false;
		return Arrays.areEqual(this.getHashableData(), contract.getHashableData());
	}
	
	/* TODO : Put the parties in hash
	 * Here it leads to a problem in equals
	 */
	@Override
	public byte[] getHashableData() {
//		StringBuffer buffer = new StringBuffer();
//		for(ElGamalKey k: parties) {
//			buffer.append(k.getPublicKey().toString());
//		}
		byte[] signable = this.clauses.getHashableData();
		return signable;
//		int signableL = signable.length;
//		int bufferL = buffer.toString().getBytes().length;
//		byte[] concate = new byte[signableL + bufferL];
//		System.arraycopy(new String(buffer).getBytes(), 0, concate, 0, bufferL);
//		System.arraycopy(signable, 0, concate, bufferL, signableL);
		
//		return concate;
	}
	
	@Override
	public SigmaSignature sign(SigmaSigner signer, ElGamalKey k) {
		signer.setKey(k);
		return signer.sign(clauses);
	}

}
