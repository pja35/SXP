package controller.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import model.api.Manager;
import model.api.ManagerDecorator;
import model.api.ManagerListener;
import model.entity.ContractEntity;
import model.entity.Item;
import model.syncManager.ContractSyncManagerImpl;
import network.api.EstablisherService;
import network.api.Messages;
import network.api.Peer;
import network.api.SearchListener;
import network.api.ServiceListener;
import network.api.advertisement.EstablisherAdvertisementInterface;
import network.api.service.Service;
import network.factories.AdvertisementFactory;

public class NetworkContractManagerDecorator extends ManagerDecorator<ContractEntity> {
	private Peer peer;
	private String who;
	
	/**
	 * 
	 * @param em Contract async manager
	 * @param peer Peer instance, started
	 * @param who who own this instance
	 */
	public NetworkContractManagerDecorator(Manager<ContractEntity> em, Peer peer, String who) {
		super(em);
		this.peer = peer;
		this.who = who;
	}

	@Override
	public void findOneById(final String id, final ManagerListener<ContractEntity> l) {
		super.findOneById(id, l);
		//TODO
	}

	@Override
	public void findAllByAttribute(String attribute, final String value, final ManagerListener<ContractEntity> l) {
		super.findAllByAttribute(attribute, value, l);
		final EstablisherService establisher = (EstablisherService) peer.getService(EstablisherService.NAME);
		Service service = peer.getService(EstablisherService.NAME);
		
		establisher.removeListener(who);
		establisher.addListener(new ServiceListener() {
			
			@Override
			public void notify(Messages messages) {
				JsonTools<ArrayList<ContractEntity>> json = new JsonTools<>(new TypeReference<ArrayList<ContractEntity>>(){});
				ArrayList<ContractEntity> contracts = json.toEntity(messages.getMessage("contract"));
				System.out.println("contract found !");
				System.out.println(messages.getMessage("contract"));
				for(ContractEntity c : contracts) {
					System.out.println(c.getId());
				}
				l.notify(json.toEntity(messages.getMessage("contract")));
			}
			
		}, who == null ? "test":who);
		
		service.search(attribute, value, new SearchListener<EstablisherAdvertisementInterface>() {
			@Override
			public void notify(Collection<EstablisherAdvertisementInterface> result) {
				ArrayList<String> uids = new ArrayList<>();
				for(EstablisherAdvertisementInterface i: result) {
					uids.add(i.getSourceURI());
				}
				ContractSyncManagerImpl co = new ContractSyncManagerImpl();
				Collection<ContractEntity> c = co.findAllByAttribute("id", value);
				JsonTools<Collection<ContractEntity>> json = new JsonTools<>(new TypeReference<Collection<ContractEntity>>(){});
				establisher.sendContract(value, who == null ? "test":who, "",json.toJson(c) , uids.toArray(new String[1]));
			}
			
		});
	}

	@Override
	public void findOneByAttribute(String attribute, String value, ManagerListener<ContractEntity> l) {
		super.findOneByAttribute(attribute, value, l);
		//TODO
	}

	@Override
	public boolean persist(ContractEntity entity) {
		return super.persist(entity);
	}

	@Override
	public boolean begin() {
		return super.begin();
	}

	@Override
	public boolean end() {
		
		if(super.end()){
			
			Collection<ContractEntity> collection = this.watchlist();
			
			for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
				
				ContractEntity contractEntity = (ContractEntity) iterator.next();
				
				EstablisherAdvertisementInterface iadv = AdvertisementFactory.createEstablisherAdvertisement();
				
				iadv.setTitle(contractEntity.getId());
				
				iadv.publish(peer);
			}
			
			return true;
		}
		
		return false;
	}
}
