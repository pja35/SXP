package network.api;

import network.api.service.Service;

public interface UserRequestService extends Service{
	
	public static final String NAME = "usersSender";
	
	public void sendRequest(String nickName, String who, String ...targetPeers);
	
	public void sendRequest(String nickName,String pbkey, String who, String ...targetPeers);
}
