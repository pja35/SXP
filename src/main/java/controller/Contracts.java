package controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import model.api.EstablisherType;
import model.api.Manager;
import model.api.Status;
import model.api.SyncManager;
import model.api.UserSyncManager;
import model.api.Wish;
import model.entity.ContractEntity;
import model.entity.User;
import model.factory.ManagerFactory;
import model.syncManager.ContractSyncManagerImpl;
import model.syncManager.UserSyncManagerImpl;
import protocol.impl.SigmaEstablisher;
import protocol.impl.sigma.SigmaContract;
import protocol.impl.sigma.SigmaEstablisherData;
import rest.api.Authentifier;
import rest.api.ServletPath;

@ServletPath("/api/contracts/*")
@Path("/")
public class Contracts {
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String add(ContractEntity contract, @HeaderParam(Authentifier.PARAM_NAME) String token) {
		Manager<ContractEntity> em = ManagerFactory.createNetworkResilianceContractManager(Application.getInstance().getPeer(), token);

		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = new UserSyncManagerImpl();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		users.close();

		if (contract.getTitle() == "")
			contract.setTitle("Secure Exchange Protocol Contract");
		contract.setCreatedAt(new Date());
		
		
		ArrayList<String> parties = contract.getParties();
		HashMap<String,String> partiesNames = new HashMap<String, String>();
		
		JsonTools<User> json3 = new JsonTools<>(new TypeReference<User>(){});
		Users us = new Users();
		for (String id : parties){
			User u = json3.toEntity(us.get(id));
			partiesNames.put(id, u.getNick());
		}
		
		em.begin();
		//TODO VALIDATION / VERIFICATION
		for (int k=0; k<parties.size(); k++){
			ContractEntity c = new ContractEntity();
			c.setTitle(contract.getTitle());
			c.setParties(parties);
			c.setPartiesNames(partiesNames);
			c.setClauses( contract.getClauses());
			c.setCreatedAt(contract.getCreatedAt());
			c.setUserid(parties.get(k));
			c.setModifierid(currentUser.getId());
			c.setWish(Wish.NEUTRAL);
			c.setStatus(Status.NOWHERE);
			c.setSignatures(null);
			em.persist(c);
		}
		em.end();
		em.close();
		
		JsonTools<ContractEntity> json = new JsonTools<>(new TypeReference<ContractEntity>(){});
		return json.toJson(contract);
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getId(@PathParam("id")String id) {
		SyncManager<ContractEntity> em = new ContractSyncManagerImpl();
		JsonTools<ContractEntity> json = new JsonTools<>(new TypeReference<ContractEntity>(){});
		String ret = json.toJson(em.findOneById(id));
		em.close();
		return ret;
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@HeaderParam(Authentifier.PARAM_NAME) String token) {
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = new UserSyncManagerImpl();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		SyncManager<ContractEntity> em = new ContractSyncManagerImpl();
		JsonTools<Collection<ContractEntity>> json = new JsonTools<>(new TypeReference<Collection<ContractEntity>>(){});
		String ret = json.toJson(em.findAllByAttribute("userid", currentUser.getId()));
		em.close();
		users.close();
		return ret;
	}

	@PUT
	@Path("/{id}")
	public void edit(ContractEntity c, @HeaderParam(Authentifier.PARAM_NAME) String token) {
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = new UserSyncManagerImpl();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		users.close();

		ArrayList<String> parties = c.getParties();
		HashMap<String,String> partiesNames = new HashMap<String, String>();
		
		JsonTools<User> json3 = new JsonTools<>(new TypeReference<User>(){});
		Users us = new Users();
		for (String id : parties){
			User u = json3.toEntity(us.get(id));
			partiesNames.put(id, u.getNick());
		}
		
		SyncManager<ContractEntity> em = new ContractSyncManagerImpl();
		em.begin();
		Collection<ContractEntity> contracts = em.findAllByAttribute("title", c.getTitle());
		for (ContractEntity contract : contracts){
			if (contract.getParties().contains(c.getUserid())){
				contract.setClauses(c.getClauses());
				contract.setParties(parties);
				contract.setTitle(c.getTitle());
				contract.setPartiesNames(partiesNames);
				contract.setModifierid(currentUser.getId());
			}
		}
		em.end();
		em.close();
	}
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(@PathParam("id")String id, @HeaderParam(Authentifier.PARAM_NAME) String token) {
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = new UserSyncManagerImpl();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		users.close();
		if (currentUser == null)
			return "{\"deleted\": \"false\"}";
		
		SyncManager<ContractEntity> em = new ContractSyncManagerImpl();
		boolean ret = em.begin();
		ContractEntity it = em.findOneById(id);
		if (!it.getUserid().equals(currentUser.getId())){
			em.end();
			em.close();
			return "{\"deleted\": \"false\"}";
		}
		return "{\"deleted\": \"" + (ret && em.remove(it) && em.end() && em.close()) + "\"}";
	}
	
	
	@PUT
	@Path("/sign/{id}")
	public void sign(String id, @HeaderParam(Authentifier.PARAM_NAME) String token){
		System.out.println("\nICI : " + id + "\n");
		
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = new UserSyncManagerImpl();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		users.close();
		
		SyncManager<ContractEntity> em = new ContractSyncManagerImpl();
		em.begin();
		ContractEntity c = em.findOneById(id);
		if (c.getSignatures() == null){
			c.setSignatures(new HashMap<String, String>());
		}
		
		SigmaEstablisher s = new SigmaEstablisher(currentUser.getKey(), null);
		s.initialize(new SigmaContract(c));
		s.start();
		JsonTools<SigmaEstablisherData> json = new JsonTools<>(new TypeReference<SigmaEstablisherData>(){});
		c.setEstablishementData(json.toJson(s.sigmaEstablisherData));
		c.setEstablisherType(EstablisherType.Sigma);
		
		em.end();
		em.close();
	}
}
