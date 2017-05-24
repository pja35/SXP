package protocol.impl.sigma;

import javax.xml.bind.annotation.XmlElement;

import model.entity.ElGamalKey;
import model.entity.sigma.Or;
import protocol.impl.sigma.steps.ProtocolStep;

public class SigmaEstablisherData {
	
	@XmlElement(name="trentkey")
	private ElGamalKey trentKey;
	
	@XmlElement(name="senderKey")
	private ElGamalKey senderKey;
	
	@XmlElement(name="protocolstep")
	private ProtocolStep protocolStep;
	
	@XmlElement(name="roundReceived")
	private Or[][] roundReceived;
	
	@XmlElement(name="contract")
	private SigmaContract contract;
	
	
	
	public void setTrentKey(ElGamalKey t){
		this.trentKey = t;
	}
	public ElGamalKey getTrentKey(){
		return this.trentKey;
	}
	
	
	public void setSenderKey(ElGamalKey s){
		this.senderKey = s;
	}
	public ElGamalKey getSenderKey(){
		return this.senderKey;
	}
	
	
	public void setProtocolStep(ProtocolStep p){
		this.protocolStep = p;
	}
	public ProtocolStep getProtocolStep(){
		return this.protocolStep;
	}
	
	
	public void setRoundReceived(Or[][] r){
		this.roundReceived = r;
	}
	public Or[][] getRoundReceived(){
		return this.roundReceived;
	}
	
	
	public void setContract(SigmaContract s){
		this.contract = s;
	}
	public SigmaContract getContract(){
		return this.contract;
	}
}
