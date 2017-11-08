package model.api;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * Interface to validate Entities
 *
 * @param <Entity>
 * @author Julien Prudhomme
 */
public interface EntityValidation<Entity> {
    /**
     * Set the entity to check
     *
     * @param entity - An entity
     */
    public void setEntity(Entity entity);

    /**
     * Validate the settled entity
     *
     * @return true if the entity is settled and valid. Otherwise false
     */
    public boolean validate();

    /**
     * Return a set of constraint violation if necessary.
     *
     * @return
     */
    public Set<ConstraintViolation<Entity>> getViolations();
}
