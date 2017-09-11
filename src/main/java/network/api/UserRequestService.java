package network.api;

import java.math.BigInteger;

import network.api.service.Service;

public interface UserRequestService extends Service{
	
	public static final String NAME = "usersSender";
	
	public void sendRequest(String nickName, String who, String ...targetPeers);
	
	public void sendRequest(String id, String nickName, String who, String ...targetPeers);
	
	public void sendRequest(String nickName,BigInteger pbkey, String who, String ...targetPeers);
}
