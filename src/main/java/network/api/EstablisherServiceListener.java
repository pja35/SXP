package network.api;

import java.util.EventListener;

import network.api.advertisement.EstablisherAdvertisementInterface;

public interface EstablisherServiceListener extends EventListener {
	
	/*
	 * Notify when the establisher receives an EstablisherAdvertisement
	 */
	public void notify(EstablisherAdvertisementInterface adv);
}
