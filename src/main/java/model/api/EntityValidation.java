package model.api;

import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * Interface to validate Entities
 * @author Julien Prudhomme
 *
 * @param <Entity>
 */
public interface EntityValidation<Entity> {
	/**
	 * Set the entity to check
	 * @param entity - An entity
	 */
	public void setEntity(Entity entity);
	
	/**
	 * Validate the settled entity
	 * @return true if the entity is settled and valid. Otherwise false
	 */
	public boolean validate();
	
	/**
	 * Return a set of constraint violation if necessary.
	 * @return
	 */
	public Set<ConstraintViolation<Entity>> getViolations();
}
