package protocol.impl.sigma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import model.entity.sigma.Or;
import model.entity.sigma.ResEncrypt;
import model.entity.sigma.ResponsesCCD;
import model.entity.sigma.SigmaSignature;

public class TrentSolver {
	
	private SigmaContract contract;
	private boolean optimistic = true;
	private Trent trent;
	
	// An index of the list is a round
	// An index of the map is a user id
	private ArrayList<HashMap<String, String>> possiblyHonestClaims;
	private HashMap<String, String[]> dishonestClaims;
	
	
	public TrentSolver(SigmaContract c, Trent t){
		this.contract = c;
		this.trent = t;
		
		this.possiblyHonestClaims = new ArrayList<HashMap<String, String>>();
		this.dishonestClaims = new HashMap<String, String[]>();
	}
	
	/**
	 * Send the answer to resolveT
	 * @param orT	: a claim by i on round : @param round
	 * @return : A string list of the form {answer type, Json answer}
	 */
	public ArrayList<String> resolveT(String m, int round, String senderId){
		int N = contract.getParties().size();
		// j was dishonest and i shows it
		for (int k=0; k+1<round; k++){
			if (possiblyHonestClaims.get(k) != null){
				HashMap<String,String> claims = possiblyHonestClaims.get(k);
				Set<String> set = claims.keySet();
				for (String s : set){
					if (s != senderId){
						String[] dishonestC = {claims.get(s), m};
						dishonestClaims.put(s, dishonestC);
						possiblyHonestClaims.get(k).remove(s);

						ArrayList<String> res =  new ArrayList<String>();
						res.add("honestyToken");
						res.add(honestyToken());
						return res;
					}
				}
			}
		}

		// i was dishonest and i shows it
		for (HashMap<String, String> claim : possiblyHonestClaims){
			int k = possiblyHonestClaims.indexOf(claim);
			if (claim.get(senderId) != null && k != round){
				String[] dishonestC = {m, claim.get(senderId)};
				dishonestClaims.put(senderId, dishonestC);
				possiblyHonestClaims.get(k).remove(senderId);
				return null; 
			}
		}

		// i was dishonest and j shows it
		for (int k=round+1; k<N; k++){
			try{
				if (possiblyHonestClaims.get(k) != null){
					HashMap<String,String> claims = possiblyHonestClaims.get(k);
					Set<String> set = claims.keySet();
					for (String s : set){
						if (s != senderId){
							String[] dishonestC = {m, claims.get(s)};
							dishonestClaims.put(senderId, dishonestC);
							possiblyHonestClaims.get(k).remove(senderId);
							return null; 
						}
					}
				}
			}catch (IndexOutOfBoundsException e){}
		}

		/***** Now the claim may be honest ****/
		// Claim with promises, wins
		if ((possiblyHonestClaims.isEmpty() && round>0) || !optimistic){
			optimistic = false;
			
			ArrayList<String> res =  new ArrayList<String>();
			res.add("resolved");
			res.add(resolveToken(m, round));
			return res;
			
		}else{
			HashMap<String, String> h = new HashMap<String, String>();
			h.put(senderId, m);
			possiblyHonestClaims.add(round, h);
			
			ArrayList<String> res =  new ArrayList<String>();
			res.add("aborted");
			res.add(honestyToken());
			return res;
		}
	}
	
	// This returns the full set of signatures
	public String resolveToken(String m, int round){

		int n = contract.getParties().size();

		JsonTools<Or[]> json3 = new JsonTools<>(new TypeReference<Or[]>(){});
		Or[] orT = json3.toEntity(m);
		ArrayList<SigmaSignature> signatures = new ArrayList<SigmaSignature>();

		byte[] data = (new String(contract.getClauses().getHashableData()) + String.valueOf(round)).getBytes();
		ResponsesCCD response;
		ResEncrypt res;

		JsonTools<ArrayList<SigmaSignature>> json = new JsonTools<>(new TypeReference<ArrayList<SigmaSignature>>(){});
		
		for (int k=0; k<n; k++){
			res = orT[k].ands[0].resEncrypt;
			response= trent.SendResponse(res, data);
			SigmaSignature s = new SigmaSignature(orT[k], response);
			s.setTrenK(trent.getKey());
			signatures.add(k,s);
		} 
		return json.toJson(signatures);
	}
	
	// Return (PossiblyHonestClaims, dishonestClaims)
	public String honestyToken(){
		JsonTools<String[]> json = new JsonTools<>(new TypeReference<String[]>(){});
		JsonTools<ArrayList<HashMap<String, String>>> jsonA = new JsonTools<>(new TypeReference<ArrayList<HashMap<String, String>>>(){});
		JsonTools<HashMap<String, String[]>> jsonB = new JsonTools<>(new TypeReference<HashMap<String, String[]>>(){});
		String[] data = {jsonA.toJson(possiblyHonestClaims), jsonB.toJson(dishonestClaims)};
		return json.toJson(data);
	}
}
