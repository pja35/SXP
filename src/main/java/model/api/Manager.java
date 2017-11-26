package model.api;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Asynchronous entity manager. Can handle network things
 *
 * @param <Entity>
 * @author Julien Prudhomme
 */
public interface Manager<Entity> {

    /**
     * Find one entity by its id
     *
     * @param id
     * @param l
     */
    public void findOneById(String id, ManagerListener<Entity> l);


    /**
     * Because we have chats with several users
     * This function lets us retrieve all the information at once without the need
     * to call findOneById for each.
     * @param id the concerning ID
     * @param l
     */
    public void findAllById(ArrayList<String> id, ManagerListener<Entity> l);

    /**
     * Find all entity that match attribute
     *
     * @param attribute
     * @param value
     * @param l
     */
    public void findAllByAttribute(String attribute, String value, ManagerListener<Entity> l);

    /**
     * Find one that match attribute
     *
     * @param attribute
     * @param value
     * @param l
     */
    public void findOneByAttribute(String attribute, String value, ManagerListener<Entity> l);



    /*TODO*/
    public void findAll(ManagerListener<Entity> l);

    /**
     * Persist the entity in the manager
     *
     * @param entity
     * @return true if done
     */
    public boolean persist(Entity entity);

    /**
     * Begin a transaction
     *
     * @return true if done
     */
    public boolean begin();

    /**
     * End a transaction
     *
     * @return true if done
     */
    public boolean end();

    /**
     * Remove an entity from the DB
     *
     * @param an entity
     * @return True if the entity has been removed, false otherwise
     */
    public boolean remove(Entity entity);

    /**
     * Is the entity in a "managed state" ?
     * i.e. Is it persistent ?
     *
     * @param entity
     */
    public boolean contains(Entity entity);

    /**
     * Returns a list of the currently "managed" entities.
     */
    public Collection<Entity> watchlist();

    /**
     * Returns a list of changed entities that currently in "managed" state.
     */
    public Collection<Entity> changesInWatchlist();

    /**
     * Checks if all the managed entities (i.e. the "watchlist") are valid entities.
     * If the validation at persist() call is activated (default) this will always return true.
     * Go to bin/META-INF/persistence.xml to change validation mode
     *
     * @return true if all the managed entities are valid, false otherwise
     */
    public boolean check();

    /**
     * Close the transaction manager
     *
     * @return fails if the operation failed
     */
    public boolean close();


}
