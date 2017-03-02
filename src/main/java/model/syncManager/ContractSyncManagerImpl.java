package model.syncManager;

import model.api.ContractSyncManager;
import model.entity.Contract;

public class ContractSyncManagerImpl extends AbstractSyncManager<Contract> implements ContractSyncManager {
	public ContractSyncManagerImpl() {
		super();
		this.initialisation("persistence", Contract.class);
	}
}
