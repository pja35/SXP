package controller.managers;

import java.util.Collection;
import model.api.Manager;
import model.api.ManagerDecorator;
import model.entity.User;
import network.api.Peer;
import network.api.advertisement.UserAdvertisementInterface;
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
	public boolean end() {
		
		if(super.end()){
			
			Collection<User> collection = this.watchlist();
		
			for (User u : collection) {
				
				UserAdvertisementInterface uadv = AdvertisementFactory.createUserAdvertisement();
				
				uadv.setNick(u.getNick());
				 
				uadv.setPbkey(String.valueOf(u.getKey().getPublicKey()));
				
				uadv.publish(peer);
				
			}
			
			return true;
		}
		
		return false;
	}
	
}
