package model.syncManager;

import model.api.ContractSyncManager;
import model.entity.Contract;
import model.factory.ValidatorFactory;
import model.validator.EntityValidator;

public class ContractSyncManagerImpl extends AbstractSyncManager<Contract> implements ContractSyncManager {
	public ContractSyncManagerImpl() {
		super();
		this.initialisation("persistence", Contract.class);
	}
	@Override
	protected EntityValidator getAdaptedValidator() {
		return ValidatorFactory.createContractValidator();
	}
}
