package crypt.impl.signatures;

import java.math.BigInteger;
import java.util.Arrays;

import crypt.base.AbstractSigner;
import model.entity.ElGamalKey;
import model.entity.sigma.Or;
import model.entity.sigma.ResEncrypt;
import model.entity.sigma.Responses;
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
		byte[] publicKey = sender.getPublicKeys().getPublicKey().toByteArray();
		byte[] b = Arrays.copyOfRange(publicKey, 0, 125);
		sender.Encryption(b, this.getTrentK());
		
		Responses rpcs = sender.SendResponseCCE(message, this.trentK);
		PCS p = new PCS(sender, this.trentK);
		Or pcs = p.getPcs(message, this.receiverK, false);
		
		SigmaSignature s = new SigmaSignature(pcs, rpcs);
		s.setTrenK(trentK);
		s.setSignerK(this.key);
		return s;
	}
	
	
	/* Verify the signature 
	 */
	@Override
	public boolean verify(byte[] message, SigmaSignature sign) {
		ElGamalKey trentKey = sign.getTrentK();
		ElGamalKey signerKey = sign.getSignerK();
		BigInteger u = sign.getPcs().ands[0].resEncrypt.getU();
		BigInteger v = sign.getPcs().ands[0].resEncrypt.getV();
		byte[] b = sign.getPcs().ands[0].resEncrypt.getM();
		ResEncrypt resE = new ResEncrypt(u, v, b);
		
		// If trent and sender keys not set, just check signature
		if (this.getReceiverK() == null || this.getTrentK() == null){
			return sign.getPcs().Verifies(message) && sign.getRpcs().Verifies(trentKey, resE);
		}
		// Check the signature and if keys match (trent and sender keys)
		
		return sign.getPcs().Verifies(message) && sign.getRpcs().Verifies(trentKey, resE)
				&& receiverK.getPublicKey().equals(signerKey.getPublicKey())
				&& trentK.getPublicKey().equals(trentKey.getPublicKey());
	}
	
	
	@Override
	public ElGamalKey getKey(){
		return this.key;
	}
}
