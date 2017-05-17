package controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.glassfish.jersey.server.ChunkedOutput;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import controller.tools.LoggerUtilities;
import model.api.Manager;
import model.api.ManagerListener;
import model.entity.Item;
import model.entity.User;
import model.factory.ManagerFactory;
import rest.api.Authentifier;
import rest.api.ServletPath;

@ServletPath("/api/search/*")
@Path("/")
public class Search{

	@GET
	@Path("/simple")
	public ChunkedOutput<String> chunckedSearchByTitle2(
			@QueryParam("title") final String title,
			@HeaderParam(Authentifier.PARAM_NAME) final String token) {
		
		final ChunkedOutput<String> output = new ChunkedOutput<String>(String.class);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				Manager<Item> em = ManagerFactory.createNetworkResilianceItemManager(Application.getInstance().getPeer(), token);
				
				em.findAllByAttribute("title", title, new ManagerListener<Item>() {
					
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
					Thread.sleep(5000);
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
		
		return output;
	}
	
	
	@GET
	@Path("/users")
	public ChunkedOutput<String> chunckedSearchUser(
			@QueryParam("nick") final String nick,
			@HeaderParam(Authentifier.PARAM_NAME) final String token) {
		
		final ChunkedOutput<String> output = new ChunkedOutput<String>(String.class);
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				Manager<User> em = ManagerFactory.createNetworkResilianceUserManager(Application.getInstance().getPeer(), token);
				
				em.findAllByAttribute("nick", nick, new ManagerListener<User>() {
					
					@Override
					public void notify(Collection<User> results) {
						
						JsonTools<Collection<User>> json = new JsonTools<>(new TypeReference<Collection<User>>(){});
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
					Thread.sleep(5000);
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
		
		return output;
	}
}
