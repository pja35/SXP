package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

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
import model.factory.ManagerFactory;
import network.api.ItemRequestService;
import network.api.Messages;
import network.api.SearchListener;
import network.api.ServiceListener;
import network.api.service.Service;
import network.impl.advertisement.ItemAdvertisement;
import network.impl.jxta.JxtaItemService;
import network.impl.jxta.JxtaItemsSenderService;
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
				Manager<Item> em = 
						ManagerFactory.createNetworkResilianceItemManager(Application.getInstance().getPeer(), token);
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

}
