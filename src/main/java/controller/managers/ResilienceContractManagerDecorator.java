package controller.managers;

import model.api.Manager;
import model.api.ManagerDecorator;
import model.entity.ContractEntity;
import network.api.Peer;

public class ResilienceContractManagerDecorator extends ManagerDecorator<ContractEntity> {

	public ResilienceContractManagerDecorator(Manager<ContractEntity> em, Peer peer) {
		super(em);
	}
}
