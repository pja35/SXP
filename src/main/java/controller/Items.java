package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.validation.ConstraintViolation;
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

import org.glassfish.jersey.server.ChunkedOutput;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.managers.CryptoItemManagerDecorator;
import controller.tools.JsonTools;
import controller.tools.LoggerUtilities;
import crypt.api.annotation.ParserAction;
import crypt.api.annotation.ParserAnnotation;
import crypt.factories.ParserFactory;
import model.api.Manager;
import model.api.ManagerListener;
import model.api.SyncManager;
import model.api.UserSyncManager;
import model.entity.Item;
import model.entity.User;
import model.factory.ManagerFactory;
import model.manager.ManagerAdapter;
import model.syncManager.ItemSyncManagerImpl;
import model.syncManager.UserSyncManagerImpl;
import model.validator.EntityValidator;
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
		
		Manager<Item> em = ManagerFactory.createCryptoNetworkResilianceItemManager(Application.getInstance().getPeer(), token,currentUser);
		//EntityManager<Item> em = new ItemManager();
		
		em.begin();
		//TODO VALIDATION
		item.setCreatedAt(new Date());
		item.setUsername(currentUser.getNick());
		item.setPbkey(currentUser.getKey().getPublicKey());
		item.setUserid(currentUser.getId());
		em.persist(item);
		em.end();
		em.close();
		users.close();
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
		String ret = json.toJson(em.findOneById(id));
		em.close();
		return ret;
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@HeaderParam(Authentifier.PARAM_NAME) String token) {
		//ChunkedOutput<String>
		//final ChunkedOutput<String> output = new ChunkedOutput<String>(String.class);
		
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = new UserSyncManagerImpl();
	    User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
	    SyncManager<Item> em = new ItemSyncManagerImpl();
		
		/*
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				ManagerAdapter<Item> adapter = new ManagerAdapter<Item>(new ItemSyncManagerImpl());
				CryptoItemManagerDecorator em = new CryptoItemManagerDecorator(adapter, currentUser);
				
				JsonTools<Collection<Item>> json = new JsonTools<>(new TypeReference<Collection<Item>>(){});
				
				em.findAllByAttribute("userid", currentUser.getId(),new ManagerListener<Item>() {

					@Override
					public void notify(Collection<Item> results) {
						JsonTools<Collection<Item>> json = new JsonTools<>(new TypeReference<Collection<Item>>(){});
						try {
							if(!results.isEmpty()) {
								output.write(json.toJson(results));
							}
							
						} catch (IOException e) {
							LoggerUtilities.logStackTrace(e);
						}
					}
				});
				
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					LoggerUtilities.logStackTrace(e);
				}
				finally {
					try {
						output.write("[]");
						output.close();
					} catch (IOException e) {
						LoggerUtilities.logStackTrace(e);
					}
				}
				em.close();
			}
			
		}).start();
		*/
	    /*
	    final StringBuilder sb=new StringBuilder();
	    
		ManagerAdapter<Item> adapter = new ManagerAdapter<Item>(new ItemSyncManagerImpl());
		CryptoItemManagerDecorator em = new CryptoItemManagerDecorator(adapter, currentUser);
		
		JsonTools<Collection<Item>> json = new JsonTools<>(new TypeReference<Collection<Item>>(){});
		
		em.findAllByAttribute("userid", currentUser.getId(),new ManagerListener<Item>() {

			@Override
			public void notify(Collection<Item> results) {
				
				JsonTools<Collection<Item>> json = new JsonTools<>(new TypeReference<Collection<Item>>(){});
				
				if(!results.isEmpty()) {
					sb.append(json.toJson(results));
				}
				
			}
		});
		
		
		*/
	    
		JsonTools<Collection<Item>> json = new JsonTools<>(new TypeReference<Collection<Item>>(){});
		String ret = json.toJson(em.findAllByAttribute("userid", currentUser.getId()));
		users.close();
		em.close();
		
		return ret;
		//return output;
		//return sb.toString();
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String edit(Item item,@HeaderParam(Authentifier.PARAM_NAME) String token) {
		
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = new UserSyncManagerImpl();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		//ManagerAdapter<Item> adapter = new ManagerAdapter<Item>(new ItemSyncManagerImpl());
		//CryptoItemManagerDecorator em = new CryptoItemManagerDecorator(adapter, currentUser);
		
		
		//if (item.getUserid() != currentUser.getId()){
		//	users.close();
		//	return "{\"edited\": \"false\"}";
		//}
		
	    SyncManager<Item> em = new ItemSyncManagerImpl();
		em.begin();
		Item item2 = em.findOneById(item.getId());
		item2.setTitle(item.getTitle());
		item2.setDescription(item.getDescription());
		
		//ParserAnnotation<Item> parser = ParserFactory.createDefaultParser(item2, currentUser);
		//item2 = parser.parseAnnotation(ParserAction.SigneAction);
		
		em.end();
		
		JsonTools<Item> json = new JsonTools<>(new TypeReference<Item>(){});
	    String ret = json.toJson(item2);
		em.close();
		users.close();
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
		UserSyncManager users = new UserSyncManagerImpl();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		users.close();
		if (currentUser == null)
			return "{\"deleted\": \"false\"}";
		
		SyncManager<Item> em = new ItemSyncManagerImpl();
		boolean ret = em.begin();
		Item it = em.findOneById(id);
		if (it.getUserid() != currentUser.getId()){
			em.end();
			em.close();
			return "{\"deleted\": \"false\"}";
		}
		return "{\"deleted\": \"" + (ret && em.remove(it) && em.end() && em.close()) + "\"}";
	}
	
	
	@GET
	@Path("/List")
	@Produces(MediaType.TEXT_HTML)
	public String lister(){
		
		final StringBuilder sb=new StringBuilder();
		
		sb.append("<!DOCTYPE html><html><head><title>Users Table</title> <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">");
		sb.append("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css\">");
		sb.append("<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>");
		sb.append("</head><body><div class=\"container\"><div class=\"row\"><div class=\"jumbotron\"><h1>List</h1><table class=\"table table-bordered\"><thead>");
		sb.append("<tr>");
		sb.append("<th>id</th>");
		sb.append("<th>title</th>");
		sb.append("<th>description</th>");
		sb.append("<th>UserName</th>");
		sb.append("<th>Sign R</th>");
		sb.append("<th>Sign S</th>");
		sb.append("</tr>");
		sb.append("</thead><tbody>");
		
		
		ManagerAdapter<Item> adapter = new ManagerAdapter<Item>(new ItemSyncManagerImpl());
		CryptoItemManagerDecorator em = new CryptoItemManagerDecorator(adapter, null);
		
		em.findAllByAttribute("username", "radoua",new ManagerListener<Item>() {
			@Override
			public void notify(Collection<Item> results) {
				for (Item item : results) {
					sb.append("<tr>");
					sb.append("<td>"+item.getId()+"</td>");
					sb.append("<td>"+item.getTitle()+"</td>");
					sb.append("<td>"+item.getDescription()+"</td>");
					sb.append("<td>"+item.getUsername()+"</td>");
					sb.append("<td>"+item.getSignature().getR()+"</td>");
					sb.append("<td>"+item.getSignature().getS()+"</td>");
					sb.append("</tr>");
				}
			}
		});
		
		
		/*
		for (Item item : collections) {
			sb.append("<tr>");
			sb.append("<td>"+item.getId()+"</td>");
			sb.append("<td>"+item.getTitle()+"</td>");
			sb.append("<td>"+item.getDescription()+"</td>");
			sb.append("<td>"+item.getUsername()+"</td>");
			sb.append("<td>"+item.getSignature()+"</td>");
			//sb.append("<td>"+item.getSignature().getS()+"</td>");
			sb.append("</tr>");
		}
		*/
		
		sb.append("</tbody></table></div></div></div></body></html>");
		
		return sb.toString();
	}
	
	
}
