package network.factories;

import network.api.advertisement.ItemAdvertisementInterface;
import network.api.advertisement.PeerAdvertisementInterface;
import network.api.advertisement.UserAdvertisementInterface;
import network.impl.advertisement.ItemAdvertisement;
import network.impl.advertisement.PeerAdvertisement;
import network.impl.advertisement.UserAdvertisement;

public class AdvertisementFactory {
	public static ItemAdvertisementInterface createItemAdvertisement() {
		return new ItemAdvertisement();
	}
	
	public static UserAdvertisementInterface createUserAdvertisement() {
		return new UserAdvertisement();
	}
	
	public static PeerAdvertisementInterface createPeerAdvertisement() {
		return new PeerAdvertisement();
	}
}
