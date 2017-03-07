package protocol.api;

import java.util.ArrayList;
import java.util.HashMap;

import crypt.api.key.AsymKey;
import crypt.api.signatures.Signer;
import model.api.Status;
import model.api.Wish;

/**
 * 
 * @author Nathanaël Eon
 *
 * @param <T> Private/public key type
 * @param <Key> Key type
 * @param <Sign> Signature type
 * @param <_Signer> Signer instance
 * @param <ContractType> Contract Type
 */
public abstract class Establisher<T, Key extends AsymKey<T>, Sign, _Signer extends Signer<Sign,Key>, ContractType extends EstablisherContract<T, Key, Sign, _Signer>> {

	protected ArrayList<EstablisherListener> listeners = new ArrayList<EstablisherListener>();
	protected ContractType contract;
	protected HashMap<Key, String> uris;
	protected _Signer signer;
	
	/**
	 * Initialize the establisher with a contract
	 * @param c
	 */
	public abstract void initialize(ContractType c);
	
	/**
	 * Start establisher signature protocol
	 */
	public abstract void start();
	
	
	/**
	 * Get the contract to be signed
	 * @return
	 */
	public ContractType getContract(){
		return contract;
	}
	
	
	/**
	 * Set the current wish of the owner of this establisher
	 * @param w
	 */
	public void setWish(Wish w){
		contract.setWish(w);
	}
	
	/**
	 * Get the current wish of the owner of this establisher
	 * @param w
	 */
	public Wish getWish(){
		return contract.getWish();
	}
	
	/**
	 * Get the current status of the protocol
	 * @return
	 */
	public Status getStatus(){
		return contract.getStatus();
	}
	
	public void setStatus(Status s){
		contract.setStatus(s);
		notifyListeners();
	}
	
	/**
	 * Add a listener of establisher events
	 * @param l
	 */
	public void addListener(EstablisherListener l){
		listeners.add(l);
	}
	
	/**
	 * Notify the listener of this establisher
	 */
	public void notifyListeners(){
		for (EstablisherListener l : listeners){
			l.establisherEvent(this.contract.getStatus());
		}
	}

}
