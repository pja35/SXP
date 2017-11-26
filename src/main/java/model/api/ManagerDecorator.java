package model.api;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Entities manager. Handle local and distant storage, search.
 *
 * @param <T>
 * @author Julien Prudhomme
 */
public abstract class ManagerDecorator<Entity> implements Manager<Entity> {

    private Manager<Entity>  em;

    public ManagerDecorator(Manager<Entity> em) {
        this.em = em;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findOneById(String id, ManagerListener<Entity> l) {
        em.findOneById(id, l);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllById(ArrayList<String> id , ManagerListener<Entity> l){
        em.findAllById(id,l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAllByAttribute(String attribute, String value, ManagerListener<Entity> l) {
        em.findAllByAttribute(attribute, value, l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findOneByAttribute(String attribute, String value, ManagerListener<Entity> l) {
        em.findOneByAttribute(attribute, value, l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAll(ManagerListener<Entity> l) {
        em.findAll(l);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean persist(Entity entity) {
        return em.persist(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean begin() {
        return em.begin();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean end() {
        return em.end();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Entity entity) {
        return em.remove(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Entity> watchlist() {
        return em.watchlist();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Entity entity) {
        return em.contains(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean check() {
        return em.check();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean close() {
        return em.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Entity> changesInWatchlist() {
        return em.changesInWatchlist();
    }


}
