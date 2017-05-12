package network.api.advertisement;

public interface EstablisherAdvertisementInterface  extends Advertisement{
	
	public String getTitle();
	public void setTitle(String title);
	
	public String getContract();
	public void setContract(String contract);
	
	// Gives the sender public key (useful to sigma protocols)
	public String  getKey();
	public void setKey(String publicKey);
}
