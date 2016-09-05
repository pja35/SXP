package network.api;

import java.util.Collection;

import network.api.advertisement.Advertisement;
import network.api.service.Service;

public interface Search<T extends Advertisement> {
	public void initialize(Service s);
	public Collection<T> search(String attribute, String value);
}
