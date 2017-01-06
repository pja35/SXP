package protocol.impl.sigma;

import model.entity.ElGamalKey;
import java.util.HashMap;
import java.math.BigInteger;

public class PCSFabric {
	//The pcs (an Or object)
	private Or pcs;
	
	//Elements used to create the Pcs
	private And[] ands = new And[2];
	private ElGamalKey senderK;
	private ElGamalKey receiverK;
	private ElGamalKey trentK;
	private ResEncrypt res;
	private Sender sender;
	
	//setters
	private void setPcs(Or privateCS){
		pcs = privateCS;
	}
	private void setSender(Sender s){
		sender = s;
		senderK = s.getKeys();
	}
	private void setReceiverKeys(ElGamalKey r){
		receiverK=r;
	}
	private void setTrentKeys(ElGamalKey t){
		trentK=t;
	}
	private void setResEncrypt (ResEncrypt r){
		res = r;
	}
	
	public ElGamalKey getReceiverKey(){
		return receiverK;
	}
	
	/**
	 * Getter
	 * @return pcs : the private contract signature
	 */
	public Or getPcs(byte[] m){
		setResEncrypt(sender.Encryption(m, trentK));
		setPcs();
		return pcs;
	}

	
	/**
	 * Make the PCS
	 */
	private void setPcs(){
		Receiver receiver = new Receiver();
		
		//Creates the Schnorr and CCE signature we will "AND"
		//2 of them are fabricated
		ResponsesCCE resCce2 = sender.SendResponseCCEFabric(res, trentK);
		ResponsesSchnorr resSchnorr2 = sender.SendResponseSchnorrFabric(receiverK);
		ResponsesCCE resCce1 = sender.SendResponseCCE(res.getM(), trentK);
		
		//For the last response, we need to choose the right challenge (to be able to compose in the or) :
		Masks mask = sender.SendMasksSchnorr();
		BigInteger c = sender.SendChallenge(mask, res.getM());
		BigInteger challenge = c.xor(resSchnorr2.getChallenge().xor(resCce1.getChallenge().xor(resCce2.getChallenge())));
		ResponsesSchnorr resSchnorr1 = sender.SendResponseSchnorr(mask, challenge);
		
		//Maps the responses with the right key (receiver for Schnorr, trent for CCE)
		HashMap<Responses,ElGamalKey> rK1 = new HashMap <Responses,ElGamalKey>();
		rK1.put(resSchnorr1, senderK);
		rK1.put(resCce1, trentK);
		
		HashMap<Responses,ElGamalKey> rK2 = new HashMap <Responses,ElGamalKey>();
		rK2.put(resSchnorr2, receiverK);
		rK2.put(resCce2, trentK);
		
		
		//Create the arrays of responses and make the "ands"
		Responses[] resp1={resSchnorr1,resCce1};
		Responses[] resp2={resSchnorr2,resCce2};
		
		ands[0] = new And(receiver,rK1,res,resp1);
		ands[1] = new And(receiver,rK2,res,resp2);
		
		//Make the PCS
		setPcs(new Or(receiver, mask.getA(), ands));
	}
	
	/**
	 * Constructor
	 * @param m : message to be signed
	 * @param s : sender keys
	 * @param r : receiver public key
	 * @param t : trent public key
	 */
	public PCSFabric(Sender s, ElGamalKey r, ElGamalKey t){
		setSender(s);
		setReceiverKeys(r);
		setTrentKeys(t);
	}
	
	/**
	 * 
	 */
	public boolean PCSVerifies(Or privateCS, byte[] m){
		setPcs(privateCS);
		return this.getPcs(m).Verifies(res);
	}
}
