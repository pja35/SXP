package controller;

import java.util.Collection;
import java.util.Date;

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
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = new UserSyncManagerImpl();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		Manager<ContractEntity> em = ManagerFactory.createNetworkResilianceContractManager(Application.getInstance().getPeer(), token);
		
		em.begin();
		//TODO VALIDATION
		if (contract.getTitle() == "")
			contract.setTitle("Secure Exchange Protocol Contract");
		contract.setCreatedAt(new Date());
		contract.setUserid(currentUser.getId());
		contract.setWish(Wish.NEUTRAL);
		contract.setStatus(Status.NOWHERE);
		contract.setSignatures(null);
		em.persist(contract);
		em.end();
		
		JsonTools<ContractEntity> json = new JsonTools<>(new TypeReference<ContractEntity>(){});
		return json.toJson(contract);
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getId(@PathParam("id")String id) {
		SyncManager<ContractEntity> em = new ContractSyncManagerImpl();
		JsonTools<ContractEntity> json = new JsonTools<>(new TypeReference<ContractEntity>(){});
		return json.toJson(em.findOneById(id));
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
		return json.toJson(em.findAllByAttribute("userid", currentUser.getId()));
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String edit(ContractEntity c) {
		SyncManager<ContractEntity> em = new ContractSyncManagerImpl();
		em.begin();
		ContractEntity contract = em.findOneById(c.getId());
		contract.setClauses(c.getClauses());
		contract.setParties(c.getParties());
		contract.setTitle(c.getTitle());
		em.end();
		JsonTools<ContractEntity> json = new JsonTools<>(new TypeReference<ContractEntity>(){});
		return json.toJson(contract);
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
	if (it.getUserid() != currentUser.getId()){
		em.end();
		em.close();
		return "{\"deleted\": \"false\"}";
	}
	return "{\"deleted\": \"" + (ret && em.remove(it) && em.end() && em.close()) + "\"}";
	}
}
