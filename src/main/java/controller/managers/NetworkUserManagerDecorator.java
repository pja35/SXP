package controller.managers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import model.api.Manager;
import model.api.ManagerDecorator;
import model.api.ManagerListener;
import model.entity.User;
import network.api.Messages;
import network.api.Peer;
import network.api.SearchListener;
import network.api.ServiceListener;
import network.api.UserRequestService;
import network.api.UserService;
import network.api.advertisement.UserAdvertisementInterface;
import network.api.service.Service;
import network.factories.AdvertisementFactory;

public class NetworkUserManagerDecorator extends ManagerDecorator<User>{

	private Peer peer;
	private String who;
	
	/**
	 * 
	 * @param em User async manager
	 * @param peer Peer instance, started
	 * @param who who own this instance
	 */
	public NetworkUserManagerDecorator(Manager<User> em, Peer peer, String who) {
		super(em);
		this.peer = peer;
		this.who = who;
	}	

	@Override
	public void findOneByAttribute(String attribute, final String value, final ManagerListener<User> l) {
		
		super.findOneByAttribute(attribute, value, l);
		
		final UserRequestService usersSender = (UserRequestService) peer.getService(UserRequestService.NAME);
		Service users = peer.getService(UserService.NAME);
		
		usersSender.removeListener(who);
		usersSender.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				JsonTools<ArrayList<User>> json = new JsonTools<>(new TypeReference<ArrayList<User>>(){});
				Collection<User> collections = json.toEntity(messages.getMessage("users")); 
				l.notify(collections);
			}
		}, who == null ? "test":who);
		
		users.search(attribute, value, new SearchListener<UserAdvertisementInterface>() {
			@Override
			public void notify(Collection<UserAdvertisementInterface> result) {
				ArrayList<String> uids = new ArrayList<>();
				for(UserAdvertisementInterface i: result) {
					uids.add(i.getSourceURI());
				}
				usersSender.sendRequest(value, who == null ? "test":who, uids.toArray(new String[1]));
			}
		});
	}

	@Override
	public void findAllByAttribute(String attribute, final String value, final ManagerListener<User> l) {
		
		super.findAllByAttribute(attribute, value, l);
		
		final UserRequestService usersSender = (UserRequestService) peer.getService(UserRequestService.NAME);
		Service users = peer.getService(UserService.NAME);
		
		usersSender.removeListener(who);
		usersSender.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				JsonTools<ArrayList<User>> json = new JsonTools<>(new TypeReference<ArrayList<User>>(){});
				Collection<User> collections = json.toEntity(messages.getMessage("users")); 
				l.notify(collections);
			}
		}, who == null ? "test":who);
		
		users.search(attribute, value, new SearchListener<UserAdvertisementInterface>() {
			@Override
			public void notify(Collection<UserAdvertisementInterface> result) {
				ArrayList<String> uids = new ArrayList<>();
				for(UserAdvertisementInterface i: result) {
					uids.add(i.getSourceURI());
				}
				usersSender.sendRequest(value, who == null ? "test":who, uids.toArray(new String[1]));
			}
		});
	}
	
	@Override
	public boolean end() {
		
		Collection<User> collection = this.changesInWatchlist();
	
		for (User u : collection) {
			
			UserAdvertisementInterface uadv = AdvertisementFactory.createUserAdvertisement();
			
			uadv.setNick(u.getNick());
			 
			uadv.setPbkey(String.valueOf(u.getKey().getPublicKey()));
			
			uadv.publish(peer);
			
		}
			
		return super.end();
	}
	
}
