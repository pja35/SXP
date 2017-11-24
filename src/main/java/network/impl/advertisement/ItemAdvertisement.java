package network.impl.advertisement;

import network.api.advertisement.ItemAdvertisementInterface;
import network.api.annotation.AdvertisementAttribute;
import network.api.annotation.ServiceName;
import network.impl.AbstractAdvertisement;

/**
 * Advertisement for a peer that host an item
 *
 * @param <Sign>
 * @author Julien Prudhomme
 */
@ServiceName(name = "items")
public class ItemAdvertisement extends AbstractAdvertisement implements ItemAdvertisementInterface {

    @AdvertisementAttribute(indexed = true)
    private String title;

    @Override
    public String getName() {
        return "item";
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
}
