package controller.managers;

import com.fasterxml.jackson.core.type.TypeReference;
import controller.tools.JsonTools;
import model.api.Manager;
import model.api.ManagerDecorator;
import model.api.ManagerListener;
import model.entity.Item;
import network.api.*;
import network.api.advertisement.ItemAdvertisementInterface;
import network.api.service.Service;
import network.factories.AdvertisementFactory;

import java.util.ArrayList;
import java.util.Collection;

public class NetworkItemManagerDecorator extends ManagerDecorator<Item> {

    private Peer peer;
    private String who;

    /**
     * @param em   Item async manager
     * @param peer Peer instance, started
     * @param who  who own this instance
     */
    public NetworkItemManagerDecorator(Manager<Item> em, Peer peer, String who) {
        super(em);
        this.peer = peer;
        this.who = who;
    }

    @Override
    public void findOneById(final String id, final ManagerListener<Item> l) {
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
    public void findAllByAttribute(String attribute, final String value, final ManagerListener<Item> l) {
        super.findAllByAttribute(attribute, value, l);
        final ItemRequestService itemSender = (ItemRequestService) peer.getService(ItemRequestService.NAME);
        Service items = peer.getService(ItemService.NAME);

        itemSender.removeListener(who);
        itemSender.addListener(new ServiceListener() {

            @Override
            public void notify(Messages messages) {
                JsonTools<ArrayList<Item>> json = new JsonTools<>(new TypeReference<ArrayList<Item>>() {
                });
//				ArrayList<Item> items = json.toEntity(messages.getMessage("items"));
//				System.out.println("items found !");
//				System.out.println(messages.getMessage("items"));
//				for(Item i : items) {
//					System.out.println(i.getTitle());
//				}
                l.notify(json.toEntity(messages.getMessage("items")));
            }

        }, who == null ? "test" : who);

        items.search(attribute, value, new SearchListener<ItemAdvertisementInterface>() {
            @Override
            public void notify(Collection<ItemAdvertisementInterface> result) {
                ArrayList<String> uids = new ArrayList<>();
                for (ItemAdvertisementInterface i : result) {
                    uids.add(i.getSourceURI());
                }
                itemSender.sendRequest(value, who == null ? "test" : who, uids.toArray(new String[1]));
            }

        });
    }

    @Override
    public void findOneByAttribute(String attribute, String value, ManagerListener<Item> l) {
        super.findOneByAttribute(attribute, value, l);
        //TODO
    }

    @Override
    public boolean persist(Item entity) {
        if (super.persist(entity)) {
            //ItemAdvertisementInterface iadv = AdvertisementFactory.createItemAdvertisement();
            //iadv.setTitle(entity.getTitle());
            //iadv.publish(peer);
            return true;
        }
        return false;
    }

    @Override
    public boolean begin() {
        return super.begin();
    }

    @Override
    public boolean end() {

        Collection<Item> collection = this.changesInWatchlist();

        for (Item item : collection) {

            ItemAdvertisementInterface iadv = AdvertisementFactory.createItemAdvertisement();

            iadv.setTitle(item.getTitle());

            iadv.publish(peer);

        }

        return super.end();
    }

    @Override
    public boolean check() {
        return super.check();
    }

    @Override
    public boolean close() {
        return super.close();
    }


}
