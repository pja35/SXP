package model.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import controller.Application;
import controller.tools.JsonTools;
import model.api.AsyncManager;
import model.api.AsyncManagerDecorator;
import model.api.AsyncManagerListener;
import model.entity.Item;
import network.api.ItemRequestService;
import network.api.Messages;
import network.api.Peer;
import network.api.SearchListener;
import network.api.Service;
import network.api.ServiceListener;
import network.impl.advertisement.ItemAdvertisement;
import network.impl.jxta.JxtaItemService;
import network.impl.jxta.JxtaItemsSenderService;

public class NetworkItemManagerDecorator extends AsyncManagerDecorator<Item>{

	private Peer peer;
	private String who;
	
	/**
	 * 
	 * @param em Item async manager
	 * @param peer Peer instance, started
	 * @param who who own this instance
	 */
	public NetworkItemManagerDecorator(AsyncManager<Item> em, Peer peer, String who) {
		super(em);
		this.peer = peer;
		this.who = who;
	}

	@Override
	public void findOneById(final String id, final AsyncManagerListener<Item> l) {
		super.findOneById(id, l);
		//TODO
		/*final ItemRequestService itemSender = (ItemRequestService) Application.getInstance().getPeer().getService(JxtaItemsSenderService.NAME);
		Service items = Application.getInstance().getPeer().getService(JxtaItemService.NAME);
		
		itemSender.addListener(new ServiceListener() {
			
			@Override
			public void notify(Messages messages) {
				JsonTools<ArrayList<Item>> json = new JsonTools<>();
				json.initialize(ArrayList.class);
				l.notify(json.toEntity(messages.getMessage("items")));
			}
			
		}, who == null ? "test":who);
		
		items.search("id", id, new SearchListener<ItemAdvertisement>() {
			@Override
			public void notify(Collection<ItemAdvertisement> result) {
				ArrayList<String> uids = new ArrayList<>();
				for(ItemAdvertisement i: result) {
					uids.add(i.getSourceURI());
				}
				itemSender.sendRequest(id, who == null ? "test":who, uids.toArray(new String[1]));
			}
			
		});*/
		
	}

	@Override
	public void findAllByAttribute(String attribute, final String value, final AsyncManagerListener<Item> l) {
		super.findAllByAttribute(attribute, value, l);
		final ItemRequestService itemSender = (ItemRequestService) peer.getService(JxtaItemsSenderService.NAME);
		Service items = peer.getService(JxtaItemService.NAME);
		
		itemSender.addListener(new ServiceListener() {
			
			@Override
			public void notify(Messages messages) {
				JsonTools<ArrayList<Item>> json = new JsonTools<>();
				json.initialize(ArrayList.class);
				l.notify(json.toEntity(messages.getMessage("items")));
			}
			
		}, who == null ? "test":who);
		
		items.search(attribute, value, new SearchListener<ItemAdvertisement>() {
			@Override
			public void notify(Collection<ItemAdvertisement> result) {
				ArrayList<String> uids = new ArrayList<>();
				for(ItemAdvertisement i: result) {
					uids.add(i.getSourceURI());
				}
				itemSender.sendRequest(value, who == null ? "test":who, uids.toArray(new String[1]));
			}
			
		});
	}

	@Override
	public void findOneByAttribute(String attribute, String value, AsyncManagerListener<Item> l) {
		super.findOneByAttribute(attribute, value, l);
		//TODO
	}

	@Override
	public void persist(Item entity) {
		super.persist(entity);
		ItemAdvertisement iadv = new ItemAdvertisement();
		iadv.setTitle(entity.getTitle());
		iadv.publish(peer);
	}

	@Override
	public void begin() {
		super.begin();
	}

	@Override
	public void end() {
		super.end();
	}
	
}
