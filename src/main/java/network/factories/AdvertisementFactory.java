package network.factories;

import network.api.advertisement.*;
import network.impl.advertisement.*;

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

    public static EstablisherAdvertisementInterface createEstablisherAdvertisement() {
        return new EstablisherAdvertisement();
    }

    public static MessageAdvertisementInterface createMessageAdvertisement() {
        return new MessageAdvertisement();
    }
}
