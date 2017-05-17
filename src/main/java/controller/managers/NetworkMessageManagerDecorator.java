package controller.managers;

import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import model.api.Manager;
import model.api.ManagerDecorator;
import model.api.ManagerListener;
import model.entity.Message;
import model.entity.User;
import network.api.MessageRequestService;
import network.api.MessageService;
import network.api.Messages;
import network.api.Peer;
import network.api.SearchListener;
import network.api.ServiceListener;
import network.api.UserRequestService;
import network.api.UserService;
import network.api.advertisement.MessageAdvertisementInterface;
import network.api.advertisement.UserAdvertisementInterface;
import network.api.service.Service;
import network.factories.AdvertisementFactory;

public class NetworkMessageManagerDecorator extends ManagerDecorator<Message>{

	private Peer peer;
	private String who;
	
	/**
	 * 
	 * @param em Message async manager
	 * @param peer Peer instance, started
	 * @param who who own this instance
	 */
	public NetworkMessageManagerDecorator(Manager<Message> em, Peer peer, String who) {
		super(em);
		this.peer = peer;
		this.who = who;
	}	
	
	
	@Override
	public void findAllByAttribute(String attribute, final String value, final ManagerListener<Message> l) {
		
		super.findAllByAttribute(attribute, value, l);
		
		final MessageRequestService messagesSender = (MessageRequestService) peer.getService(MessageRequestService.NAME);
		Service messages = peer.getService(MessageService.NAME);
		
		messagesSender.removeListener(who);
		messagesSender.addListener(new ServiceListener() {
			@Override
			public void notify(Messages messages) {
				JsonTools<ArrayList<Message>> json = new JsonTools<>(new TypeReference<ArrayList<Message>>(){});
				
				Collection<Message> collections = json.toEntity(messages.getMessage("messages"));
				
				l.notify(collections);
			}
		}, who);
		
		messages.search(attribute, value, new SearchListener<MessageAdvertisementInterface>() {
			@Override
			public void notify(Collection<MessageAdvertisementInterface> result) {
				ArrayList<String> uids = new ArrayList<>();
				for(MessageAdvertisementInterface i: result) {
					uids.add(i.getSourceURI());
				}

				messagesSender.sendRequest(value,value, who, uids.toArray(new String[1]));
			}
		});
	}
	
	
	@Override
	public boolean end() {
			
		Collection<Message> collection = this.changesInWatchlist();
	
		for (Message m : collection) {
			
			MessageAdvertisementInterface madv = AdvertisementFactory.createMessageAdvertisement();
			
			madv.setSenderId(m.getSenderId());
			
			madv.setReceiverId(m.getReceiverId());
			
			madv.publish(peer);
			System.out.println("message publish");
		}
		
		return super.end();
	}
	
}
