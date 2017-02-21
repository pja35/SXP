package protocol.impl;

import protocol.api.Contract;
import protocol.api.Establisher;
import protocol.api.EstablisherListener;
import protocol.api.Status;
import protocol.api.Wish;
import protocol.impl.sigma.SigmaEstablisher;

public class SigmaEstablisherAdapter implements Establisher {
	
	private SigmaEstablisher establisher;
	
	@Override
	public void initialize(Contract<?, ?, ?, ?> c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public Contract<?, ?, ?, ?> getContract() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWish(Wish w) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getWish(Wish w) {
		// TODO Auto-generated method stub

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
