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
import model.api.SyncManager;
import model.api.UserSyncManager;
import model.entity.Item;
import model.entity.User;
import model.factory.ManagerFactory;
import model.syncManager.ItemSyncManagerImpl;
import model.syncManager.UserSyncManagerImpl;
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
		UserSyncManager users = new UserSyncManagerImpl();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		Manager<Item> em = ManagerFactory.createNetworkResilianceItemManager(Application.getInstance().getPeer(), token);
		//EntityManager<Item> em = new ItemManager();
		em.begin();
		//TODO VALIDATION
			item.setCreatedAt(new Date());
			item.setUsername(currentUser.getNick());
			item.setPbkey(currentUser.getKey().getPublicKey());
			item.setUserid(currentUser.getId());
			em.persist(item);
		em.end();
		
		/*ItemAdvertisement iadv = new ItemAdvertisement();
		iadv.setTitle(item.getTitle());
		iadv.publish(Application.getInstance().getPeer()); */
		
		JsonTools<Item> json = new JsonTools<>(new TypeReference<Item>(){});
		return json.toJson(item);
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getId(
			@PathParam("id")String id) {
		SyncManager<Item> em = new ItemSyncManagerImpl();
		JsonTools<Item> json = new JsonTools<>(new TypeReference<Item>(){});
		return json.toJson(em.findOneById(id));
	}
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@HeaderParam(Authentifier.PARAM_NAME) String token) {
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = new UserSyncManagerImpl();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		SyncManager<Item> em = new ItemSyncManagerImpl();
		JsonTools<Collection<Item>> json = new JsonTools<>(new TypeReference<Collection<Item>>(){});
		return json.toJson(em.findAllByAttribute("userid", currentUser.getId()));
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String edit(Item item) {
		SyncManager<Item> em = new ItemSyncManagerImpl();
		em.begin();
		Item item2 = em.findOneById(item.getId());
		item2.setTitle(item.getTitle());
		item2.setDescription(item.getDescription());
		em.end();
		
		JsonTools<Item> json = new JsonTools<>(new TypeReference<Item>(){});
		return json.toJson(item2);
		
		//return JsonUtils.BeanStringify(item2);
	}
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(
			@PathParam("id")int id) {
			//TODO
		return null;
	}
	
}
