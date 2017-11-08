package network.api;

import network.api.advertisement.Advertisement;

import java.util.Collection;

/**
 * Search interface for advertisement
 *
 * @param <T> Type of advertisement searched
 * @author Julien Prudhomme
 */
public interface SearchListener<T extends Advertisement> {
    /**
     * Call to notify one or more object are found
     *
     * @param result the advertisement found.
     */
    public void notify(Collection<T> result);
}
