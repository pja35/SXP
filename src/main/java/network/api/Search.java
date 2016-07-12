package network.api;

import java.util.Collection;

public interface Search<T extends Advertisement> {
	public void initialize(Service s);
	public Collection<T> search(String attribute, String value);
}
