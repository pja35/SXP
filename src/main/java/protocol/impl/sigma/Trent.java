/* Copyright 2015 Pablo Arrighi, Sarah Boukris, Mehdi Chtiwi, 
   Michael Dubuis, Kevin Perrot, Julien Prudhomme.

   This file is part of SXP.

   SXP is free software: you can redistribute it and/or modify it 
   under the terms of the GNU Lesser General Public License as published 
   by the Free Software Foundation, version 3.

   SXP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
   PURPOSE.  See the GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License along with SXP. 
   If not, see <http://www.gnu.org/licenses/>. */
package protocol.impl.sigma;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.core.type.TypeReference;

import crypt.impl.signatures.SigmaSigner;

import controller.Application;
import controller.tools.JsonTools;
import controller.tools.LoggerUtilities;
import crypt.api.encryption.Encrypter;
import crypt.factories.EncrypterFactory;
import model.entity.ContractEntity;
import model.entity.ElGamalKey;
import model.entity.sigma.And;
import model.entity.sigma.Masks;
import model.entity.sigma.Or;
import model.entity.sigma.ResEncrypt;
import model.entity.sigma.ResponsesCCD;
import model.entity.sigma.SigmaSignature;
import network.api.EstablisherService;
import network.api.EstablisherServiceListener;
import network.api.Peer;
import protocol.impl.sigma.steps.ProtocolResolve;


/**
 * this class simulate the arbiter but in the end all users have this class
 * the arbiter can described message, and in the protocol CCD
 * @author sarah
 *
 */
public class Trent {
	
	public final static String TRENT_MESSAGE = "FROM_TRENT";
	
	protected EstablisherService establisherService;
	
	protected final ElGamalKey keys;
	private HashMap<Masks,BigInteger> eph = new HashMap<Masks, BigInteger>();
	
	private HashMap<String, TrentSolver> solvers = new HashMap<String, TrentSolver>();

	private Encrypter<ElGamalKey> encrypter;
	
	/**
	 * Constructor
	 */
	public Trent(final ElGamalKey key){
		this.keys = key;
		
		encrypter = EncrypterFactory.createElGamalSerpentEncrypter();
		encrypter.setKey(keys);
	 }
	
	public void setListener(){
		 establisherService = (EstablisherService) Application.getInstance().getPeer().getService(EstablisherService.NAME);
		// Add a listener in case someone ask to resolve
		establisherService.setListener("title", ProtocolResolve.TITLE + this.keys.getPublicKey().toString(), "TRENT"+this.keys.getPublicKey().toString(),new EstablisherServiceListener() {
			@Override
			public void notify(String title, String content, String senderId) {
				BigInteger msgSenKey = new BigInteger(senderId);
				ElGamalKey senderK = new ElGamalKey();
				senderK.setPublicKey(msgSenKey);
				senderK.setG(keys.getG());
				senderK.setP(keys.getP());
				
				resolve(content, senderK);
			}
		}, false);
	}
	
	/*
	 * Trent resolve function
	 * Send a message to each signer of the contract
	 * 		Message format : ArrayList<String> = {title, content}
	 */
	private void resolve(String message, ElGamalKey senderK){
		Peer peer = Application.getInstance().getPeer();
		
		JsonTools<String[]> json = new JsonTools<>(new TypeReference<String[]>(){});
		String[] content = json.toEntity(message);
		
		// Message received useless
		if (content != null){
			
			// Data stored in the message
			int round = Integer.parseInt(content[0]);
			
			JsonTools<ContractEntity> json2 = new JsonTools<>(new TypeReference<ContractEntity>(){});
			SigmaContract contract = new SigmaContract(json2.toEntity(content[1]));
			String m = new String(encrypter.decrypt(content[2].getBytes()));

			JsonTools<SigmaSignature> json4 = new JsonTools<>(new TypeReference<SigmaSignature>(){});
			String sign = new String(encrypter.decrypt(content[3].getBytes()));
			SigmaSignature signature = json4.toEntity(sign);
			
			
			// Setup the necessary to check signature
			SigmaSigner s = new SigmaSigner();
			s.setKey(this.keys);
			s.setReceiverK(senderK);
			s.setTrentK(this.keys);

			boolean verifiedOr = true;
			if (round > 0){
				byte[] data = (new String(contract.getHashableData()) + round).getBytes();
				JsonTools<Or[]> json3 = new JsonTools<>(new TypeReference<Or[]>(){});
				Or[] orT = json3.toEntity(m);
				
				// Checks the signature
				for (Or o : orT){
					verifiedOr = verifiedOr 
								&& o.Verifies(data) 
								&& this.VerifiesRes(o, senderK.getPublicKey());
				}
			}
			if (s.verify(m.getBytes(), signature) && verifiedOr){
				String id = new String(contract.getHashableData());
				if (solvers.get(id) == null){
					solvers.put(id, new TrentSolver(contract, this));
				}

				// TrentSolver is the class dealing with the message
				TrentSolver ts = solvers.get(id);
				ArrayList<String> resolved = ts.resolveT(m, round, senderK.getPublicKey().toString());

				if (resolved == null){
					establisherService.sendContract(TRENT_MESSAGE + new String(contract.getHashableData()), 
							"dishonest " + senderK.getPublicKey().toString(),
							this.keys.getPublicKey().toString(),
							peer);
				} else{
					HashMap<String,String> signatures = new HashMap<String,String>();
					for (ElGamalKey k : contract.getParties()){
						s.setReceiverK(k);
						SigmaSignature signa = s.sign(resolved.get(1).getBytes());
						JsonTools<SigmaSignature> jsons = new JsonTools<>(new TypeReference<SigmaSignature>(){});
						signatures.put(k.getPublicKey().toString(),jsons.toJson(signa));
					}
					JsonTools<HashMap<String,String>> jsona = new JsonTools<>(new TypeReference<HashMap<String,String>>(){});
					resolved.add(jsona.toJson(signatures));
					
					JsonTools<ArrayList<String>> jsons = new JsonTools<>(new TypeReference<ArrayList<String>>(){});
					String answer = jsons.toJson(resolved);

					establisherService.sendContract(TRENT_MESSAGE + new String(contract.getHashableData()),
							answer,
							this.keys.getPublicKey().toString(),
							peer);
				}
			}
		}
	}

	/**
	 * Create mask for the CCD response
	 * @param res
	 * @return Masks
	 */
	public Masks SendMasks(ResEncrypt res)
	{
		BigInteger s;
		s = Utils.rand(160, keys.getP());
		
		BigInteger a, aBis;
		
		a = keys.getG().modPow(s, keys.getP());
		aBis = res.getU().modPow(s, keys.getP());
		
		Masks masks = new Masks(a,aBis);
		eph.put(masks, s);
		
		return masks;
	}
	
	/**
	 * Create challenge for the not interactive version for the CCD
	 * @param mask
	 * @param message
	 * @return
	 */
	public BigInteger SendChallenge(Masks mask, byte[] message)
	{
		BigInteger challenge;
		byte[] buffer, resume;
		MessageDigest hash_function = null;
		
		String tmp = message.toString().concat(mask.getA().toString());
		
		buffer = tmp.getBytes();
		
		try {
			hash_function = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			LoggerUtilities.logStackTrace(e);
		}
		
		resume = hash_function.digest(buffer);
		challenge = new BigInteger(resume);
		return challenge;
	}
	
	/**
	 * Create reponse CCD 
	 * @param challenge
	 * @param mask
	 * @return BigInteger
	 */
	public BigInteger SendAnswer(BigInteger challenge, Masks mask)
	{
		BigInteger r = (keys.getPrivateKey().multiply(challenge)).add(eph.get(mask));
		return r;	
	}

	/**
	 * Create response CCD will send
	 * @param resEncrypt
	 * @return
	 */
	public ResponsesCCD SendResponse(ResEncrypt resEncrypt)
	{		
		Masks mask = this.SendMasks(resEncrypt);
		BigInteger challenge = this.SendChallenge(mask, resEncrypt.getM());
		BigInteger response = this.SendAnswer(challenge, mask);
		
		return new ResponsesCCD(mask,challenge,response);
	}

	/**
	 * Create response CCD will send
	 * @param resEncrypt
	 * @return
	 */
	public ResponsesCCD SendResponse(ResEncrypt resEncrypt, byte[] m)
	{		
		Masks mask = this.SendMasks(resEncrypt);
		BigInteger challenge = this.SendChallenge(mask, m);
		BigInteger response = this.SendAnswer(challenge, mask);
		
		return new ResponsesCCD(mask,challenge,response);
	}
	
	public boolean VerifiesRes(Or o, BigInteger senPubK){
		boolean isVerified = false;
		for (And a : o.ands){
			byte[] data = Sender.getIdentificationData(a.rK.get(a.responses[0]));
			BigInteger k = new BigInteger(data);
			BigInteger h = decryption(a.resEncrypt);
			isVerified = isVerified || h.equals(k);
		}
		return isVerified;
	}
	
	/**
	 * decrypt
	 * @param cipherText
	 * @return
	 */
	public  byte[] decryption (byte[]cipherText)
	{
		ElGamal elGamal = new ElGamal (keys);
        return elGamal.decryptWithPrivateKey(cipherText);
	}
	
	public BigInteger decryption(ResEncrypt res){
		BigInteger u = res.getU();
		BigInteger v = res.getV();
		BigInteger p = keys.getP();
		BigInteger data = u.modPow(p.subtract(BigInteger.ONE).subtract(keys.getPrivateKey()), p).multiply(v).mod(p);
		return data;
	}
	
	/**
	 * gives trent public keys
	 * @return
	 */
	public ElGamalKey getKey(){
		ElGamalKey pubKey = new ElGamalKey();
		pubKey.setG(this.keys.getG());
		pubKey.setP(this.keys.getP());
		pubKey.setPublicKey(this.keys.getPublicKey());
		return pubKey;
	}
}
