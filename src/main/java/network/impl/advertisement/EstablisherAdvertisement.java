package network.impl.advertisement;

import network.api.advertisement.EstablisherAdvertisementInterface;
import network.api.annotation.AdvertisementAttribute;
import network.api.annotation.ServiceName;
import network.impl.AbstractAdvertisement;

@ServiceName(name = "establisher")
public class EstablisherAdvertisement extends AbstractAdvertisement implements EstablisherAdvertisementInterface{

	@AdvertisementAttribute(indexed = true)
	private String title;
	
	private String promI;
	
	@Override
	public String getName() {
		return "contractEstablisher";
	}

	@Override
	public String getAdvertisementType() {
		return null;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public String getContract() {
		return promI;
	}
	
	public void setContract(String prom) {
		this.promI = prom;
	}
}
