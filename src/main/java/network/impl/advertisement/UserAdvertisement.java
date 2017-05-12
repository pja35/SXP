package network.impl.advertisement;

import network.api.Peer;
import network.api.advertisement.UserAdvertisementInterface;
import network.api.annotation.AdvertisementAttribute;
import network.api.annotation.ServiceName;
import network.impl.AbstractAdvertisement;

@ServiceName(name = "users")
public class UserAdvertisement extends AbstractAdvertisement implements UserAdvertisementInterface{

	@AdvertisementAttribute(indexed = true)
	private String nick;
	
	@AdvertisementAttribute(indexed = true)
	private String pbkey;
	
	@Override
	public String getName() {
		return "user";
	}
		
	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getPbkey() {
		return pbkey;
	}

	public void setPbkey(String pbkey) {
		this.pbkey = pbkey;
	}

	@Override
	public String getAdvertisementType() {
		return null;
	}

	@Override
	public void publish(Peer peer) {
		super.publish(peer);
	}
	
	
	
}
