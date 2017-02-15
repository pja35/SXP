package network.impl.advertisement;

import network.api.advertisement.ContractAdvertisementInterface;
import network.api.annotation.AdvertisementAttribute;
import network.api.annotation.ServiceName;
import network.impl.AbstractAdvertisement;

@ServiceName(name = "establisher")
public class ContractAdvertisement extends AbstractAdvertisement implements ContractAdvertisementInterface{

	@AdvertisementAttribute(indexed = true)
	private String title;
	
	private String promI;
	
	@Override
	public String getName() {
		return "item";
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

	public String getPromI() {
		return promI;
	}
	
	public void setPromI(String prom) {
		this.promI = prom;
	}
}
