package protocol.impl.sigma;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

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
public class SigmaContractAdapter extends EstablisherContract<BigInteger, ElGamalKey, ElGamalSignature, ElGamalSigner>{
	
	
	private ContractEntity contract;
	
	// Basic constructor
	public SigmaContractAdapter(){
		super();
		this.signer = new ElGamalSigner();
		this.contract = new ContractEntity();
		contract.setClauses(new ArrayList<String>());
		contract.setParties(new ArrayList<String>());
		contract.setSignatures(new HashMap<String,String>());
	}
	
	// Constructor from clauses
	public SigmaContractAdapter(Signable<ElGamalSignature> clauses){
		super();
		this.signer = new ElGamalSigner();
		this.contract = new ContractEntity();
		this.setClauses(clauses);
		contract.setParties(new ArrayList<String>());
		contract.setSignatures(new HashMap<String,String>());
	}
	
	// Constructor from a ContractEntity (what will be most use)
	public SigmaContractAdapter(ContractEntity c){
		super();
		this.contract=c;
		this.signer = new ElGamalSigner();
		this.setTitle(contract.getTitle());
		this.setClauses(contract.getClauses());
		this.setParties(contract.getParties());
	}

	
	/************* SETTERS ***********/
	@Override
	public void setTitle(String t){
		super.setTitle(t);
		contract.setTitle(t);
	}
	public void setClauses(ArrayList<String> c){
		this.clauses = new ElGamalClauses(c);
		this.contract.setClauses(c);
	}
	@Override
	public void setClauses(Signable<ElGamalSignature> c){
		this.clauses = c;
		ArrayList<String> a = new ArrayList<String>();
		a.add(new String(c.getHashableData()));
		this.contract.setClauses(a);
		
	}
	public void setParties(ArrayList<String> s){
		for (String u : s){
			JsonTools<User> json = new JsonTools<>(new TypeReference<User>(){});
			Users users = new Users();
			User user = json.toEntity(users.get(u));
			this.parties.add(user.getKey());
			this.partiesId.put(user.getKey(), user.getId());
		}
	}
	
	/************* Adding classes ***********/
	@Override 
	public void addParty(ElGamalKey k){
		this.parties.add(k);
		this.contract.getParties().add(k.getPublicKey().toString());
	}
	
	@Override
	public void addSignature(ElGamalKey k, ElGamalSignature s){
		super.addSignature(k, s);
		contract.getSignatures().put(this.partiesId.get(k), s.toString());
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
	
	
	
}
