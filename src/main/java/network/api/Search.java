package network.api;

import network.api.advertisement.Advertisement;
import network.api.service.Service;

import java.util.Collection;

public interface Search<T extends Advertisement> {
    public void initialize(Service s);

    public Collection<T> search(String attribute, String value);
}
