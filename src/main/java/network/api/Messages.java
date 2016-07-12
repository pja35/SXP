package network.api;

public interface Messages {
	/**
	 * Get message content by this name (field)
	 * @param name
	 * @return
	 */
	public String getMessage(String name);
	
	/**
	 * Get all the messages fields
	 * @return
	 */
	public String[] getNames();
	
	/**
	 * Set the receiver of the messages
	 * @param who
	 */
	public void setWho(String who);
	
	public String getWho();
}
