package protocol.impl.sigma;

import java.math.BigInteger;
import java.util.HashMap;

import crypt.factories.SignerFactory;
import crypt.impl.signatures.SigmaSigner;
import model.entity.ElGamalKey;
import model.entity.sigma.And;
import model.entity.sigma.Masks;
import model.entity.sigma.Or;
import model.entity.sigma.ResEncrypt;
import model.entity.sigma.Responses;
import model.entity.sigma.ResponsesCCE;
import model.entity.sigma.ResponsesSchnorr;
import model.entity.sigma.SigmaSignature;

/**
 * 
 * @author Nathanaël EON
 */

public class PCS {
	//The PCS (resEncrypt + Or)
	public ResEncrypt res;
	public Or pcs;
	
	//Elements used to create the PCS
	private ElGamalKey trentK;
	private Sender sender;
	private And[] ands = new And[2];
	
	

	/**
	 * Constructor
	 * @param s : sender keys
	 * @param r : receiver public key
	 * @param t : trent public key
	 */
	public PCS(Sender s, ElGamalKey t){
		setSender(s);
		setTrentKeys(t);
		setResEncrypt(sender.getResEncrypt());
	}
	
	
	/**
	 * 
	 */
	public Or getPcs(byte[] m, ElGamalKey r, boolean changeEncrypter){
		
		// Encrypter is here for Trent to identify the sender
		if (changeEncrypter){
			byte[] b = Sender.getIdentificationData(sender.getPublicKeys());
			res = sender.Encryption(b, this.trentK);
		}
		
		// The message to be encrypted is m
		createPcs(m, r);
		return pcs;
	}
	
	/**
	 * Checks the current pcs according to a message
	 */
	public boolean Verifies(byte[] m){
		if (pcs==null) {return false;}
		return pcs.Verifies(m);
	}
	/**
	 * Checks a PCS according to a message
	 */
	public boolean Verifies(Or privateCS, byte[] m){
		if (privateCS==null) {return false;}
		return privateCS.Verifies(m);
	}
	
	
	//setters
	private void setPcs(Or privateCS){
		pcs = privateCS;
	}
	private void setSender(Sender s){
		sender = s;
	}
	private void setTrentKeys(ElGamalKey t){
		trentK=t;
	}
	private void setResEncrypt(ResEncrypt r){
		res = r;
		
	}
	
	
	/**
	 * Create the PCS from what is in here 
	 */
	private void createPcs(byte[] m, ElGamalKey receiverK){
		//Creates the Schnorr and CCE signature we will "AND"
		//2 of them are fabricated
		ResponsesSchnorr resSchnorr2 = sender.SendResponseSchnorrFabric(receiverK);
		ResponsesCCE resCce2 = sender.SendResponseCCEFabric(res, trentK);
		ResponsesCCE resCce1 = sender.SendResponseCCE(m, trentK);

		//Forge the last response using a special challenge (composition in the or) :
		Masks mask = sender.SendMasksSchnorr();
		BigInteger c = sender.SendChallenge(mask, m);
		BigInteger challenge = c.xor(resSchnorr2.getChallenge().xor(resCce1.getChallenge().xor(resCce2.getChallenge())));
		ResponsesSchnorr resSchnorr1 = sender.SendResponseSchnorr(mask, challenge);

		//Maps the responses with the right key (receiver for Schnorr, trent for CCE)
		HashMap<Responses,ElGamalKey> rK1 = new HashMap <Responses,ElGamalKey>();
		rK1.put(resSchnorr1, sender.getPublicKeys());
		rK1.put(resCce1, trentK);

		HashMap<Responses,ElGamalKey> rK2 = new HashMap <Responses,ElGamalKey>();
		rK2.put(resSchnorr2, receiverK);
		rK2.put(resCce2, trentK);
		
		//Create the arrays of responses and make the "ands"
		Responses[] resp1={resSchnorr1,resCce1};
		Responses[] resp2={resSchnorr2,resCce2};

		ands = new And[2];
		ands[0] = new And(rK1,res,resp1);
		ands[1] = new And(rK2,res,resp2);

		//Make the PCS
		Or o = new Or(mask.getA(), ands);
		o.contract=m;
		setPcs(o);
	}
	
	
	/**
	 * @param contract
	 * @return Signature on contract
	 */
	public SigmaSignature getClearSignature(SigmaContract contract, ElGamalKey r){
		SigmaSigner sig =SignerFactory.createSigmaSigner(); 
		sig.setKey(sender.getKeys());
		sig.setTrentK(this.trentK);
		sig.setReceiverK(r);
		return sig.sign(contract.getHashableData());
	}
	
	/**
	 * Check the authenticity of signature
	 * @param signature
	 * @param contract
	 * @param key
	 * @return
	 */
	public boolean verifySignature(SigmaSignature signature, SigmaSigner signer, SigmaContract contract){
		if (signature == null ){
			return false;
		}
		return signer.verify(contract.getHashableData(), signature);
	}
	
}
