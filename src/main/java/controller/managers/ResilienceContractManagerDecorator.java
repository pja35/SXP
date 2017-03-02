package controller.managers;

import model.api.Manager;
import model.api.ManagerDecorator;
import model.entity.Contract;
import network.api.Peer;

public class ResilienceContractManagerDecorator extends ManagerDecorator<Contract> {

	public ResilienceContractManagerDecorator(Manager<Contract> em, Peer peer) {
		super(em);
	}
}
