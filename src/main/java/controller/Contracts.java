package controller;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import model.api.Manager;
import model.api.SyncManager;
import model.api.UserSyncManager;
import model.entity.Contract;
import model.entity.User;
import model.factory.ManagerFactory;
import model.syncManager.ContractSyncManagerImpl;
import model.syncManager.UserSyncManagerImpl;

import java.util.Collection;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import rest.api.Authentifier;
import rest.api.ServletPath;

@ServletPath("/api/contracts/*")
@Path("/")
public class Contracts {
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String add(Contract contract, @HeaderParam(Authentifier.PARAM_NAME) String token) {
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = new UserSyncManagerImpl();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		Manager<Contract> em = ManagerFactory.createNetworkResilianceContractManager(Application.getInstance().getPeer(), token);
		
		em.begin();
		//TODO VALIDATION
		contract.setCreatedAt(new Date());
		contract.setUserid(currentUser.getId());
		contract.setSigned(false);
		em.persist(contract);
		em.end();
		
		JsonTools<Contract> json = new JsonTools<>(new TypeReference<Contract>(){});
		return json.toJson(contract);
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getId(@PathParam("id")String id) {
		SyncManager<Contract> em = new ContractSyncManagerImpl();
		JsonTools<Contract> json = new JsonTools<>(new TypeReference<Contract>(){});
		return json.toJson(em.findOneById(id));
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@HeaderParam(Authentifier.PARAM_NAME) String token) {
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = new UserSyncManagerImpl();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		SyncManager<Contract> em = new ContractSyncManagerImpl();
		JsonTools<Collection<Contract>> json = new JsonTools<>(new TypeReference<Collection<Contract>>(){});
		return json.toJson(em.findAllByAttribute("userid", currentUser.getId()));
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String edit(Contract c) {
		SyncManager<Contract> em = new ContractSyncManagerImpl();
		em.begin();
		Contract contract = em.findOneById(c.getId());
		contract.setClauses(c.getClauses());
		contract.setParties(c.getParties());
		contract.setTitle(c.getTitle());
		em.end();
		JsonTools<Contract> json = new JsonTools<>(new TypeReference<Contract>(){});
		return json.toJson(contract);
	}
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(@PathParam("id")String id) {
		SyncManager<Contract> em = new ContractSyncManagerImpl();
		em.begin();
		em.delete(id);
		em.end();
		return null;
	}
}
