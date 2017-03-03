package protocol.api;

import model.api.Status;
import model.api.Wish;

public interface Establisher {
	/**
	 * Initialize the establisher with a contract
	 * @param c
	 */
	public void initialize(EstablisherContract<?,?,?,?> c);
	
	/**
	 * Start establisher signature protocol
	 */
	public void start();
	
	/**
	 * Get the contract to be signed
	 * @return
	 */
	public EstablisherContract<?,?,?,?> getContract();
	
	/**
	 * Set the current wish of the owner of this establisher
	 * @param w
	 */
	public void setWish(Wish w);
	
	/**
	 * Get the current wish of the owner of this establisher
	 * @param w
	 */
	public Wish getWish();
	
	/**
	 * Get the current status of the protocol
	 * @return
	 */
	public Status getStatus();
	
	/**
	 * Add a listener of establisher events
	 * @param l
	 */
	public void addListener(EstablisherListener l);
	
	/**
	 * Notify the listener of this establisher
	 */
	public void notifyListeners();
}