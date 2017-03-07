package protocol.impl.sigma;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import org.bouncycastle.util.Arrays;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.Users;
import controller.tools.JsonTools;
import crypt.api.signatures.Signable;
import crypt.impl.signatures.ElGamalSignature;
import crypt.impl.signatures.ElGamalSigner;
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
public class SigmaContract extends EstablisherContract<BigInteger, ElGamalKey, ElGamalSignature, ElGamalSigner>{
	
	// List of parties keys
	protected ArrayList<ElGamalKey> parties = new ArrayList<>();
	// Maps the keys with the id of a user
	protected HashMap<ElGamalKey,String> partiesId = new HashMap<ElGamalKey, String>();
	// Maps the keys with the signatures
	protected HashMap<ElGamalKey, ElGamalSignature> signatures = new HashMap<ElGamalKey, ElGamalSignature>();
	// Clauses in the format we need them
	protected Signable<ElGamalSignature> clauses = null;
	// Signer object
	protected ElGamalSigner signer;
	
	// Basic constructor
	public SigmaContract(){
		super();
		this.signer = new ElGamalSigner();
		this.contract = new ContractEntity();
		contract.setClauses(new ArrayList<String>());
		contract.setParties(new ArrayList<String>());
		contract.setSignatures(new HashMap<String,String>());
	}
	
	// Constructor from clauses
	public SigmaContract(Signable<ElGamalSignature> clauses){
		super();
		this.signer = new ElGamalSigner();
		this.contract = new ContractEntity();
		this.setClauses(clauses);
		contract.setParties(new ArrayList<String>());
		contract.setSignatures(new HashMap<String,String>());
	}
	
	// Constructor from a ContractEntity (what will be most use)
	public SigmaContract(ContractEntity c){
		super();
		this.contract=c;
		this.signer = new ElGamalSigner();
		this.setClauses(contract.getClauses());
		this.setParties(contract.getParties());
	}

	/************* GETTERS ***********/
	public Signable<ElGamalSignature> getClauses(){
		return clauses;
	}
	public ArrayList<ElGamalKey> getParties(){
		return parties;
	}
	
	/************* SETTERS ***********/
	public void setClauses(ArrayList<String> c){
		this.clauses = new ElGamalClauses(c);
		this.contract.setClauses(c);
	}
	public void setClauses(Signable<ElGamalSignature> c){
		this.clauses = c;
		ArrayList<String> a = new ArrayList<String>();
		a.add(new String(c.getHashableData()));
		this.contract.setClauses(a);
	}
	
	/**
	 * Get the parties keys
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
	}
	/**
	 * Set the parties from a list of ElGamalKey
	 * WARNING : this won't set the partiesId
	 */
	public void setParties(ArrayList<ElGamalKey> p, boolean isSigmaParty){
		this.parties = p;
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
		for(ElGamalKey k: parties) {
			signer.setKey(k);
			if(signatures.get(k) == null || !signer.verify(clauses.getHashableData(), signatures.get(k))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void addSignature(ElGamalKey k, ElGamalSignature s) {
		if(k == null || !this.parties.contains(k)) {
			throw new RuntimeException("invalid key");
		}
		signatures.put(k, s);
		contract.getSignatures().put(this.partiesId.get(k), s.toString());
	}
	
	@Override
	public boolean checkContrat(EstablisherContract<BigInteger, ElGamalKey, ElGamalSignature, ElGamalSigner> contract) {
		return this.equals(contract) && this.isFinalized();
	}
	
	@Override
	public boolean equals(EstablisherContract<BigInteger, ElGamalKey, ElGamalSignature, ElGamalSigner> c) {
		return Arrays.areEqual(this.getHashableData(), c.getHashableData());
	}
	
	@Override
	public byte[] getHashableData() {
		StringBuffer buffer = new StringBuffer();
		for(ElGamalKey k: parties) {
			buffer.append(k.getPublicKey().toString());
		}
		byte[] signable = this.clauses.getHashableData();
		
		int signableL = signable.length;
		int bufferL = buffer.toString().getBytes().length;
		byte[] concate = new byte[signableL + bufferL];
		System.arraycopy(buffer.toString().getBytes(), 0, concate, 0, bufferL);
		System.arraycopy(signable, 0, concate, bufferL, signableL);
		
		return concate;
	}
	
	@Override
	public ElGamalSignature sign(ElGamalSigner signer, ElGamalKey k) {
		return signer.sign(clauses);
	}

}
