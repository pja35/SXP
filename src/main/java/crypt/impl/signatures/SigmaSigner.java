package crypt.impl.signatures;

import java.math.BigInteger;

import crypt.base.AbstractSigner;
import model.entity.ElGamalKey;
import model.entity.sigma.Or;
import model.entity.sigma.ResEncrypt;
import model.entity.sigma.Responses;
import model.entity.sigma.SigmaSignature;
import protocol.impl.sigma.PCS;
import protocol.impl.sigma.Sender;

public class SigmaSigner  extends AbstractSigner<SigmaSignature, ElGamalKey>{

	
	private ElGamalKey trentK;
	private ElGamalKey receiverK;
	
	public ElGamalKey getTrentK(){
		return trentK;
	}
	public void setTrentK(ElGamalKey t){
		this.trentK = t;
	}
	
	public ElGamalKey getReceiverK(){
		return receiverK;
	}
	public void setReceiverK(ElGamalKey r){
		this.receiverK = r;
	}
	
	
	/* Sign the message 
	 */
	@Override
	public SigmaSignature sign(byte[] message) {
		if (this.getReceiverK() == null || this.getTrentK() == null){
			throw new RuntimeException("Trent and receiver keys not set");
		}
		
		Sender sender = new Sender(this.key);
		// Need to setup the "encrypt
		byte[] b = Sender.getIdentificationData(this.key);
		sender.Encryption(b, this.getTrentK());
		
		Responses rpcs = sender.SendResponseCCE(message, this.trentK);
		PCS p = new PCS(sender, this.trentK);
		Or pcs = p.getPcs(message, this.receiverK, false);
		
		SigmaSignature s = new SigmaSignature(pcs, rpcs);
		s.setTrenK(trentK);
		return s;
	}
	
	
	/* Verify the signature 
	 */
	@Override
	public boolean verify(byte[] message, SigmaSignature sign) {
		ElGamalKey trentKey = sign.getTrentK();
		
		ResEncrypt resE = sign.getPcs().ands[0].resEncrypt;
		
		// checks the resEncrypt according to the receiverK
		if (this.getReceiverK() != null){
			BigInteger m = new BigInteger(resE.getM()).mod(trentKey.getP());
			BigInteger n = new BigInteger(Sender.getIdentificationData(getReceiverK()));
			if (!m.equals(n)){
				return false;
			}
		}

		// If trent and sender keys not set, just check signature
		if (this.getTrentK() == null){
			return sign.getPcs().Verifies(message) && sign.getRpcs().Verifies(trentKey, resE);
		}
		
		// Check the signature and if keys match (Trent and sender keys)
		return sign.getPcs().Verifies(message) && sign.getRpcs().Verifies(trentKey, resE)
				&& trentK.getPublicKey().equals(trentKey.getPublicKey());
	}
	
	
	@Override
	public ElGamalKey getKey(){
		return this.key;
	}
}
