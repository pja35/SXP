package network.api;

import network.api.service.Service;

public interface MessageRequestService extends Service{
	
	public static final String NAME = "messagesSender";
		
	/**
	 * Send messages request
	 * @param receiverId message receiver ID
	 * @param who sender
	 * @param uris target peers
	 */
	public void sendRequest(String receiverId, String who, String ...uris);

	
	/**
	 * Send messages request
	 * @param senderId message sender ID
	 * @param receiverId message receiver ID
	 * @param who sender
	 * @param uris target peers
	 */
	public void sendRequest(String senderId, String receiverId, String who, String ...uris);

}
