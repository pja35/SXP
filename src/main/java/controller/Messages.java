package controller;

import java.util.ArrayList;
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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.managers.CryptoItemManagerDecorator;
import controller.managers.CryptoMessageManagerDecorator;
import controller.tools.JsonTools;
import model.api.ManagerListener;
import model.api.SyncManager;
import model.api.UserSyncManager;
import model.entity.Item;
import model.entity.Message;
import model.entity.User;
import model.manager.ManagerAdapter;
import model.syncManager.ItemSyncManagerImpl;
import model.syncManager.MessageSyncManagerImpl;
import model.syncManager.UserSyncManagerImpl;
import rest.api.Authentifier;
import rest.api.ServletPath;

@ServletPath("/api/messages/*")
@Path("/")
public class Messages {
	private final static Logger log = LogManager.getLogger(Message.class);
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String add(Message message, @HeaderParam(Authentifier.PARAM_NAME) String token) {
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = new UserSyncManagerImpl();
		User sender = users.getUser(auth.getLogin(token), auth.getPassword(token));

		message.setSendingDate(new Date());
		message.setSender(sender.getId(), sender.getNick());
		SyncManager<User> um = new UserSyncManagerImpl();
		User reciever = um.findOneByAttribute("nick", message.getReceiverName());
		um.close();
		if (reciever != null){
			message.setReceiver(reciever.getId(), reciever.getNick());
			
			//SyncManager<Message> em = new MessageSyncManagerImpl();
			ManagerAdapter<Message> adapter = new ManagerAdapter<Message>(new MessageSyncManagerImpl());
			CryptoMessageManagerDecorator em = new CryptoMessageManagerDecorator(adapter, reciever); 
			log.debug(message.getString());
			boolean pushDbOk = em.begin();
			pushDbOk &= em.persist(message);
			pushDbOk &= em.end();
			pushDbOk &= em.close();
			if (!pushDbOk){
				log.warn("Message might not have been sent.");
				return "{\"error\": \"Message might not have been sent.\"}";
			}

			JsonTools<Message> json = new JsonTools<>(new TypeReference<Message>(){});
			return json.toJson(message);
		}		
		return "{\"error\": \"No receiver specified.\"}";
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@HeaderParam(Authentifier.PARAM_NAME) String token) {
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = new UserSyncManagerImpl();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		users.close();
		
		//SyncManager<Message> em = new MessageSyncManagerImpl();
		
		ManagerAdapter<Message> adapter = new ManagerAdapter<Message>(new MessageSyncManagerImpl());
		CryptoMessageManagerDecorator em = new CryptoMessageManagerDecorator(adapter, currentUser);
		
		JsonTools<Collection<Message>> json = new JsonTools<>(new TypeReference<Collection<Message>>(){});
		
		final ArrayList<Message> list = new ArrayList<>();
 		
		
		em.findAllByAttribute("receiverName", currentUser.getNick(), new ManagerListener<Message> (){
			@Override
			public void notify(Collection<Message> results) {
				list.addAll(results);
			}
			
		});
		
		em.findAllByAttribute("senderName", currentUser.getNick(), new ManagerListener<Message> (){
			@Override
			public void notify(Collection<Message> results) {
				list.addAll(results);
			}
			
		});
		
		
		//Collection<Message> collec = em.findAllByAttribute("receiverName", currentUser.getNick());
		//collec.addAll(em.findAllByAttribute("senderName", currentUser.getNick()));
		em.close();
		return json.toJson(list);
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String edit(Message message) {	
		return null;
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(
			@PathParam("id") long id) {
		return null;
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
		sb.append("<th>senderName</th>");
		sb.append("<th>receiverName</th>");
		sb.append("<th>messageContent</th>");
		sb.append("<th>status</th>");
		sb.append("</tr>");
		sb.append("</thead><tbody>");
		
		SyncManager<Message> syncManager= new MessageSyncManagerImpl();
		
		Collection<Message> results = syncManager.findAll();
		
		for (Message message : results) {
			sb.append("<tr>");
			sb.append("<td>"+message.getId()+"</td>");
			sb.append("<td>"+message.getSendName()+"</td>");
			sb.append("<td>"+message.getReceiverName()+"</td>");
			sb.append("<td>"+message.getMessageContent()+"</td>");
			sb.append("<td>"+message.getStatus()+"</td>");
			sb.append("</tr>");
		}
		
		
		sb.append("</tbody></table></div></div></div></body></html>");
		
		syncManager.close();
		return sb.toString();
	}
}
