package network.impl.advertisement;

import network.api.advertisement.EstablisherAdvertisementInterface;
import network.api.annotation.AdvertisementAttribute;
import network.api.annotation.ServiceName;
import network.impl.AbstractAdvertisement;

@ServiceName(name = "establisher")
public class EstablisherAdvertisement extends AbstractAdvertisement implements EstablisherAdvertisementInterface{

	@AdvertisementAttribute(indexed = true)
	private String title;
	

	@AdvertisementAttribute(enabled = true)
	private String contract;
	
	@AdvertisementAttribute(enabled = true)
	private String key;
	
	// Sender userid
	private String userid;
	
	@Override
	public String getName() {
		return "contractEstablisher";
	}

	@Override
	public String getAdvertisementType() {
		return null;
	}
	
	@Override
	public String getTitle() {
		return title;
	}
	
	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getContract() {
		return contract;
	}
	
	@Override
	public void setContract(String c) {
		this.contract = c;
	}

	@Override
	public String  getKey(){
		return key;
	}
	
	@Override
	public void setKey(String publicKey){
		this.key = publicKey;
	}

	public String getUserid() {
		return userid;
	}
	
	public void setUserid(String u) {
		this.userid = u;
	}
}
