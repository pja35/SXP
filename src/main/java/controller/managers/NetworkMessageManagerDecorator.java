package controller.managers;

import java.util.Collection;
import model.api.Manager;
import model.api.ManagerDecorator;
import model.entity.Message;
import network.api.Peer;
import network.api.advertisement.MessageAdvertisementInterface;
import network.factories.AdvertisementFactory;

public class NetworkMessageManagerDecorator extends ManagerDecorator<Message>{

	private Peer peer;
	private String who;
	
	/**
	 * 
	 * @param em User async manager
	 * @param peer Peer instance, started
	 * @param who who own this instance
	 */
	public NetworkMessageManagerDecorator(Manager<Message> em, Peer peer, String who) {
		super(em);
		this.peer = peer;
		this.who = who;
	}	
	
	@Override
	public boolean end() {
		
		if(super.end()){
			
			Collection<Message> collection = this.changesInWatchlist();
		
			for (Message m : collection) {
				
				MessageAdvertisementInterface madv = AdvertisementFactory.createMessageAdvertisement();
				
				madv.setSenderId(m.getSenderId());
				
				madv.setReceiverId(m.getReceiverId());
				
				madv.publish(peer);
				
			}
			
			return true;
		}
		
		return false;
	}
	
}
