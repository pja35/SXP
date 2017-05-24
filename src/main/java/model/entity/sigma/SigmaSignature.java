package model.entity.sigma;

import javax.xml.bind.annotation.XmlElement;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import crypt.base.BaseSignature;
import model.entity.ElGamalKey;

/**
 * @author Nathanaël Eon
 */
public class SigmaSignature extends BaseSignature<String>{

	/* Element that compose a Sigma Signature */
	@XmlElement(name="pcs")
	private Or pcs;
	@XmlElement(name="rpcs")
	private Responses rpcs;
	
	@XmlElement(name="trentK")
	private ElGamalKey trentK;
	
	/* Simple constructor for Json */
	public SigmaSignature(){}
	
	/* Comstructor with params */
	public SigmaSignature(Or p, Responses r){
		this.pcs = p;
		this.rpcs = r;
	}
	
	public Or getPcs(){
		return pcs;
	}
	public void setPcs(Or p){
		this.pcs = p;
	}
	
	public Responses getRpcs(){
		return rpcs;
	}
	public void setRpcs(Responses r){
		this.rpcs = r;
	}
	
	public ElGamalKey getTrentK(){
		return trentK;
	}
	public void setTrenK(ElGamalKey t){
		this.trentK = t;
	}
	
	@Override
	public String getParam(String p){
		if (p.equals("pcs")){
			JsonTools<Or> json = new JsonTools<>(new TypeReference<Or>(){});
			return json.toJson(pcs, true);
		}else if (p.equals("rpcs")){
			JsonTools<Responses> json = new JsonTools<>(new TypeReference<Responses>(){});
			return json.toJson(rpcs);
		}else if (p.equals("trentK")){
			JsonTools<ElGamalKey> json = new JsonTools<>(new TypeReference<ElGamalKey>(){});
			return json.toJson(trentK);
		}
		return null;
	}
	
}
