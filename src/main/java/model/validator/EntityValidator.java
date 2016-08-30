package model.validator;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import model.api.EntityValidation;

public class EntityValidator<Entity> implements EntityValidation<Entity>{
	protected Entity entity = null;
	private Set<ConstraintViolation<Entity>> violations = null;
	private Validator validator = null;
	@Override
	public void setEntity(Entity entity) {
		this.entity = entity;
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		violations = validator.validate(this.entity);
	}

	@Override
	public boolean validate() {
		if(entity == null) return false;
		return violations.size() == 0;
	}

	@Override
	public Set<ConstraintViolation<Entity>> getViolations() {
		return violations;
	}

}
