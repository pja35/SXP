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
import model.entity.ElGamalKey;
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
		
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = SyncManagerFactory.createUserSyncManager();
		final User sender = users.getUser(auth.getLogin(token), auth.getPassword(token));	

		final ChunkedOutput<String> output = new ChunkedOutput<String>(String.class);
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				final ArrayList<User> asyncResult = new ArrayList<>();
				
				Manager<User> usem = ManagerFactory.createNetworkResilianceUserManager(Application.getInstance().getPeer(), token);
				
				usem.findAllByAttribute("nick",message.getReceiverName(), new ManagerListener<User>() {
					@Override
					public void notify(Collection<User> results) {
						
						for (Iterator iterator = results.iterator(); iterator.hasNext();) {
							User user = (User) iterator.next();
							asyncResult.add(user);
							break;
						}
					}
				});
				
				try {
					Thread.sleep(3000);
					usem.close();
				} catch (InterruptedException e) {
					LoggerUtilities.logStackTrace(e);
				}
				
				User reciever = asyncResult.size()>0? asyncResult.get(0):null;
				
				if (reciever != null &&  !(reciever.getId().equals(sender.getId())) ){
					
					message.setSendingDate(new Date());
					message.setSender(sender.getId(), sender.getNick());
					message.setPbkey(sender.getKey().getPublicKey());
					message.setReceiver(reciever.getId(), reciever.getNick());
					Manager<Message> em = ManagerFactory.createNetworkResilianceMessageManager(Application.getInstance().getPeer(), token,reciever,sender); 
								
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
					
					em.close();
					
					JsonTools<Message> json = new JsonTools<>(new TypeReference<Message>(){});
					try {
						output.write(json.toJson(message));
					} catch (IOException e) {
						LoggerUtilities.logStackTrace(e);
					}
				
				}else{
					
					try {
						output.write("{\"error\": \"No receiver specified.\"}");
					} catch (IOException e) {
						LoggerUtilities.logStackTrace(e);
					}
				
				}
			
				try {
					output.write("[]");
					output.close();
				} catch (IOException e) {
					LoggerUtilities.logStackTrace(e);
				}
				
			}
		}).start();
		
		return output;
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public ChunkedOutput<String> get(@HeaderParam(Authentifier.PARAM_NAME) final String token) {

		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = SyncManagerFactory.createUserSyncManager();
		final User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		users.close();
		
		final ChunkedOutput<String> output = new ChunkedOutput<String>(String.class);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				JsonTools<Collection<Message>> json = new JsonTools<>(new TypeReference<Collection<Message>>(){});
				
				Manager<Message> em = ManagerFactory.createNetworkResilianceMessageManager(Application.getInstance().getPeer(), token, currentUser,null);
				
				final Hashtable<String, Message> hashtableMessage = new Hashtable<>(); 
				
				em.findAllByAttribute("receiverId", currentUser.getId(), new ManagerListener<Message>() {
					@Override
					public void notify(Collection<Message> results) {
						
						for (Iterator iterator = results.iterator(); iterator.hasNext();) {
							Message message = (Message) iterator.next();
							if(hashtableMessage.get(message.getId())==null){
								hashtableMessage.put(message.getId(), message);
							}
						}
					}
				});
				
				em.findAllByAttribute("senderId", currentUser.getId(), new ManagerListener<Message>() {
					@Override
					public void notify(Collection<Message> results) {
						for (Iterator iterator = results.iterator(); iterator.hasNext();) {
							Message message = (Message) iterator.next();
							if(hashtableMessage.get(message.getId())==null){
								hashtableMessage.put(message.getId(), message);
							}
						}
					}
				});
				
				try {
					
					Thread.sleep(3000);
					
					output.write(json.toJson(hashtableMessage.values()));
					
				} catch (InterruptedException e) {
					LoggerUtilities.logStackTrace(e);
				} catch (IOException e) {
					e.printStackTrace();
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
		
		return output;
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
