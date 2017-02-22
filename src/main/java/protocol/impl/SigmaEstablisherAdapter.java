package protocol.impl;

import java.util.HashMap;

import model.entity.ElGamalKey;
import protocol.api.Contract;
import protocol.api.Establisher;
import protocol.api.EstablisherListener;
import protocol.api.Status;
import protocol.api.Wish;
import protocol.impl.sigma.SigmaEstablisher;
import protocol.impl.contract.ElGamalContract;

public class SigmaEstablisherAdapter implements Establisher {

	private Status status;
	private SigmaEstablisher establisher;
	private ElGamalContract contract;
	private HashMap<ElGamalKey, String> uris;
	
	@Override
	public void initialize(Contract<?, ?, ?, ?> c, HashMap<ElGamalKey, String> u) {
		contract = (ElGamalContract) c;
		uris = u;
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public Contract<?, ?, ?, ?> getContract() {
		return contract;
	}

	@Override
	public void setWish(Wish w) {
		// TODO Auto-generated method stub

	}

	@Override
	public Wish getWish(Wish w) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addListener(EstablisherListener l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyListeners() {
		// TODO Auto-generated method stub

	}

}
