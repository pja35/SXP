package controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

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
import controller.tools.LoggerUtilities;
import model.api.ItemSyncManager;
import model.api.Manager;
import model.api.ManagerListener;
import model.api.UserSyncManager;
import model.entity.ElGamalSignEntity;
import model.entity.Item;
import model.entity.User;
import model.factory.ManagerFactory;
import model.factory.SyncManagerFactory;
import rest.api.Authentifier;
import rest.api.ServletPath;

@ServletPath("/api/items/*")
@Path("/")
public class Items {

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String add(Item item, @HeaderParam(Authentifier.PARAM_NAME) String token) {
		
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = SyncManagerFactory.createUserSyncManager();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		
		Manager<Item> em = ManagerFactory.createCryptoNetworkResilianceItemManager(Application.getInstance().getPeer(), token,currentUser);
		
		em.begin();
		//TODO VALIDATION
		item.setUsername(currentUser.getNick());
		item.setPbkey(currentUser.getKey().getPublicKey());
		item.setUserid(currentUser.getId());
		item.setSignature(new ElGamalSignEntity());
		item.setCreatedAt(new Date());
		em.persist(item);
		em.end();
		em.close();
		users.close();
		
		JsonTools<Item> json = new JsonTools<>(new TypeReference<Item>(){});
		return json.toJson(item);
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getId(
			@PathParam("id")String id) {
		ItemSyncManager em = SyncManagerFactory.createItemSyncManager();
		JsonTools<Item> json = new JsonTools<>(new TypeReference<Item>(){});
		String ret = json.toJson(em.findOneById(id));
		em.close();
		return ret;
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@HeaderParam(Authentifier.PARAM_NAME) String token) {
		
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = SyncManagerFactory.createUserSyncManager();
	    User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
	    ItemSyncManager em = SyncManagerFactory.createItemSyncManager();
		JsonTools<Collection<Item>> json = new JsonTools<>(new TypeReference<Collection<Item>>(){});
		String ret = json.toJson(em.findAllByAttribute("userid", currentUser.getId()));
		users.close();
		em.close();
		return ret;
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String edit(Item item,@HeaderParam(Authentifier.PARAM_NAME) String token) {
		
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = SyncManagerFactory.createUserSyncManager();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		users.close();
		
		ItemSyncManager itmn = SyncManagerFactory.createItemSyncManager();
		Manager<Item> entityManager = ManagerFactory.createCryptoNetworkResilianceItemManager(itmn,Application.getInstance().getPeer(), token,currentUser);
		 
		Item it = itmn.findOneById(item.getId());

		entityManager.begin();
		it.setTitle(item.getTitle());
		it.setDescription(item.getDescription());
		entityManager.end();
		
		entityManager.close();
		
		if (it==null)
			return "{\"edit\": \"false\"}";
		
		JsonTools<Item> json = new JsonTools<>(new TypeReference<Item>(){});
	    String ret = json.toJson(it);
		
		return ret;
	}

	/** 
	 * This only deletes items from local base.
	 * TO DO : connect to jxta
	 * @param id
	 * @param token
	 * @return
	 */
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(@PathParam("id")String id, @HeaderParam(Authentifier.PARAM_NAME) String token) {
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = SyncManagerFactory.createUserSyncManager();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		users.close();
		if (currentUser == null)
			return "{\"deleted\": \"false\"}";
		
		ItemSyncManager em = SyncManagerFactory.createItemSyncManager();
		boolean ret = em.begin();
		Item it = em.findOneById(id);
		if (it.getUserid() != currentUser.getId()){
			em.end();
			em.close();
			return "{\"deleted\": \"false\"}";
		}
		return "{\"deleted\": \"" + (ret && em.remove(it) && em.end() && em.close()) + "\"}";
	}
	
}
