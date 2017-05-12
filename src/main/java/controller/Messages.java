package controller;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

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
import org.glassfish.jersey.server.ChunkedOutput;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.managers.CryptoItemManagerDecorator;
import controller.managers.CryptoMessageManagerDecorator;
import controller.tools.JsonTools;
import controller.tools.LoggerUtilities;
import crypt.factories.ElGamalAsymKeyFactory;
import model.api.Manager;
import model.api.ManagerListener;
import model.api.SyncManager;
import model.api.UserSyncManager;
import model.entity.ElGamalSignEntity;
import model.entity.Item;
import model.entity.Message;
import model.entity.User;
import model.factory.ManagerFactory;
import model.factory.SyncManagerFactory;
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
	public ChunkedOutput<String> add(final Message message, @HeaderParam(Authentifier.PARAM_NAME) final String token) {
		
		final ChunkedOutput<String> output = new ChunkedOutput<String>(String.class);
		
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = SyncManagerFactory.createUserSyncManager();
		final User sender = users.getUser(auth.getLogin(token), auth.getPassword(token));	
		
		final Hashtable<String, Object> query = new Hashtable<>();
		query.put("id", message.getReceiverId());
		query.put("nick", message.getReceiverName());
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				Manager<User> em = ManagerFactory.createNetworkResilianceUserManager(Application.getInstance().getPeer(), token);
				
				em.findAllByAttributes(query, new ManagerListener<User>() {
					
					@Override
					public void notify(Collection<User> results) {
						
						User reciever = results.iterator().next(); 
						
						//reciever.getKey().setPrivateKey(sender.getKey().getPrivateKey());//TODO: add function setKey() to parser interface and change the key in cryptoMessageDecorator 
						
						if (reciever != null){
							
							message.setSendingDate(new Date());
							message.setSender(sender.getId(), sender.getNick());
							message.setReceiver(reciever.getId(), reciever.getNick());
							message.setPbkey(reciever.getKey().getPublicKey());
							
							Manager<Message> em = ManagerFactory.createCryptoMessageManager(reciever); 
							
							log.debug(message.getString());
							boolean pushDbOk = em.begin();
							
							pushDbOk &= em.persist(message);
							
							pushDbOk &= em.end();
							pushDbOk &= em.close();
							
							if (!pushDbOk){
								log.warn("Message might not have been sent.");
								try {
									output.write("{\"error\": \"Message might not have been sent.\"}");
								} catch (IOException e) {
									LoggerUtilities.logStackTrace(e);
								}
							}

							JsonTools<Message> json = new JsonTools<>(new TypeReference<Message>(){});
							try {
								output.write(json.toJson(message));
							} catch (IOException e) {
								LoggerUtilities.logStackTrace(e);
							}
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
			
		//return "{\"error\": \"No receiver specified.\"}";
		
		return output;
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(@HeaderParam(Authentifier.PARAM_NAME) String token) {
		
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = SyncManagerFactory.createUserSyncManager();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		users.close();
		
		Manager<Message> em = ManagerFactory.createNetworkResilianceMessageManager(Application.getInstance().getPeer(), token, currentUser);
		
		JsonTools<Collection<Message>> json = new JsonTools<>(new TypeReference<Collection<Message>>(){});
		
		final ArrayList<Message> list = new ArrayList<>();
 		
		em.findAllByAttribute("receiverId", currentUser.getId(), new ManagerListener<Message> (){
			@Override
			public void notify(Collection<Message> results) {
				list.addAll(results);
			}
		});
		
		/*
		em.findAllByAttribute("senderId", currentUser.getId(), new ManagerListener<Message> (){
			@Override
			public void notify(Collection<Message> results) {
				list.addAll(results);
			}
		});
		*/
		
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
		
}
