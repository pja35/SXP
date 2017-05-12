package network.impl.jxta;

import java.util.Collection;
import java.util.Hashtable;

import javax.print.attribute.standard.RequestingUserName;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import model.api.UserSyncManager;
import model.entity.User;
import model.factory.SyncManagerFactory;
import net.jxta.pipe.PipeMsgEvent;
import network.api.Messages;
import network.api.UserRequestService;
import network.impl.MessagesGeneric;
import network.impl.messages.RequestUserMessage;

public class JxtaUsersSenderService extends JxtaService implements UserRequestService{
	
	public static final String NAME = "usersSender";
	
	public JxtaUsersSenderService() {
		this.name = NAME;
	}
	
	@Override
	public void sendRequest(String nick, String who, String ... peerURIs) {
		
		RequestUserMessage m = new RequestUserMessage();
		
		m.setNick(nick);
		
		m.setPbkey("");
		
		m.setWho(who);
		
		m.setSource(this.peerUri);
		
		this.sendMessages(m, peerURIs);
	}
	
	@Override
	public void sendRequest(String nick, String pbkey,String who, String ... peerURIs) {
		
		RequestUserMessage m = new RequestUserMessage();
		
		m.setNick(nick);
		
		m.setPbkey(pbkey);
		
		m.setWho(who);
		
		m.setSource(this.peerUri);
		
		this.sendMessages(m, peerURIs);
	}
	
	private Messages getResponseMessage(Messages msg) {
		
		MessagesGeneric m = new MessagesGeneric();
		
		m.setWho(msg.getWho());
		
		m.addField("type", "response");
		
		UserSyncManager em = SyncManagerFactory.createUserSyncManager();
		
		Collection<User> users;
		
		if(msg.getMessage("pbkey").isEmpty()){
			users = em.findAllByAttribute("nick", msg.getMessage("nick"));
		}else{
			Hashtable<String, String> query = new Hashtable<>();
			query.put("nick", msg.getMessage("nick"));
			query.put("keys.publicKey", msg.getMessage("pbkey"));
			users = em.findAllByAttributes(query);
		}
		
		JsonTools<Collection<User>> json = new JsonTools<>(new TypeReference<Collection<User>>(){});
		
		m.addField("users", json.toJson(users));
		
		return m;
	}
	
	
	@Override
	public void pipeMsgEvent(PipeMsgEvent event) {
		
		Messages message = toMessages(event.getMessage());
		
		System.out.println("[JxtaUsersSenderService:pipeMsgEvent]===>"+message.getMessage("type")+" : "+message.getMessage("nick"));
		
		if(message.getMessage("type").equals("response")) {
			super.pipeMsgEvent(event);
			return;
		}
		
		this.sendMessages(getResponseMessage(message), message.getMessage("source"));
	}
}
