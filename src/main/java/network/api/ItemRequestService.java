package network.api;

public interface ItemRequestService extends Service{
	/**
	 * Send items request
	 * @param title item title
	 * @param who sender
	 * @param uris target peers
	 */
	public void sendRequest(String title, String who, String ...uris);
}
