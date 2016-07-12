package model.manager;

import java.util.ArrayList;

import model.api.AsyncManager;
import model.api.AsyncManagerListener;
import model.api.EntityManager;

public class AsyncManagerAdapter<Entity> implements AsyncManager<Entity>{

	private EntityManager<Entity> em;
	
	public AsyncManagerAdapter(EntityManager<Entity> manager) {
		em = manager;
	}
	
	@Override
	public void findOneById(String id, AsyncManagerListener<Entity> l) {
		ArrayList<Entity> r = new ArrayList<>();
		r.add(em.findOneById(id));
		l.notify(r);
	}

	@Override
	public void findAllByAttribute(String attribute, String value, AsyncManagerListener<Entity> l) {
		l.notify(em.findAllByAttribute(attribute, value));
	}

	@Override
	public void findOneByAttribute(String attribute, String value, AsyncManagerListener<Entity> l) {
		ArrayList<Entity> r = new ArrayList<>();
		r.add(em.findOneByAttribute(attribute, value));
		l.notify(r);
	}

	@Override
	public void persist(Entity entity) {
		em.persist(entity);
	}

	@Override
	public void begin() {
		em.begin();
	}

	@Override
	public void end() {
		em.end();
	}
	
}
