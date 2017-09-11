package model.syncManager;

import model.api.ContractSyncManager;
import model.entity.ContractEntity;
import model.factory.ValidatorFactory;
import model.validator.EntityValidator;

public class ContractSyncManagerImpl extends AbstractSyncManager<ContractEntity> implements ContractSyncManager {
	public ContractSyncManagerImpl() {
		super();
		this.initialisation("persistence", ContractEntity.class);
	}
	@Override
	protected EntityValidator<?> getAdaptedValidator() {
		return ValidatorFactory.createContractValidator();
	}
}
