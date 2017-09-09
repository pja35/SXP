package network.impl.jxta;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import javax.print.attribute.standard.RequestingUserName;

import org.eclipse.persistence.internal.jpa.metadata.structures.ArrayAccessor;

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
		
		m.setAttribute("");
		
		m.setType("request");
		
		m.setWho(who);
		
		m.setSource(this.peerUri);
		
		this.sendMessages(m, peerURIs);
	}
	
	@Override
	public void sendRequest(String id, String nick, String who, String... targetPeers) {
		
		RequestUserMessage m = new RequestUserMessage();
		
		m.setNick(nick);
		
		m.setAttribute(id);
		
		m.setType("requestById");
		
		m.setWho(who);
		
		m.setSource(this.peerUri);
		
		this.sendMessages(m, targetPeers);
	}
	
	@Override
	public void sendRequest(String nick, BigInteger pbkey,String who, String ... peerURIs) {
		
		RequestUserMessage m = new RequestUserMessage();
		
		m.setNick(nick);
		
		m.setAttribute(String.valueOf(pbkey));
		
		m.setType("requestByPbkey");
		
		m.setWho(who);
		
		m.setSource(this.peerUri);
		
		this.sendMessages(m, peerURIs);
	}
	
	private Messages getResponseMessage(Messages msg) {
		
		ArrayList<User> resultat = new ArrayList<>();
		
		MessagesGeneric m = new MessagesGeneric();
		
		UserSyncManager em = SyncManagerFactory.createUserSyncManager();
		
		m.setWho(msg.getWho());
		
		m.addField("type", "response");
		
		if(msg.getMessage("type").equals("request")){
			
			resultat.addAll(em.findAllByAttribute("nick", msg.getMessage("nick")));
		
		}else if(msg.getMessage("type").equals("requestById")){
			
			Collection<User> tmpList=em.findAllByAttribute("nick", msg.getMessage("nick"));
			
			for (Iterator iterator = tmpList.iterator(); iterator.hasNext();) {
				User user = (User) iterator.next();
				
				if(user.getId().equals(msg.getMessage("attribute"))){
					resultat.add(user);
				}
			}
			
		}else if(msg.getMessage("type").equals("requestByPbkey")){
			
			Collection<User> users = em.findAllByAttribute("nick", msg.getMessage("nick"));
			
			for (Iterator iterator = users.iterator(); iterator.hasNext();) {
				User user = (User) iterator.next();
				if(user.getKey().getPublicKey().equals(new BigInteger(msg.getMessage("attribute"),16))){
					resultat.add(user);
					break; //return first user with the same nickName and pbkey
				}
			}
			
		}
		
		em.close();
		
		JsonTools<Collection<User>> json = new JsonTools<>(new TypeReference<Collection<User>>(){});
		
		m.addField("users", json.toJson(resultat));
		
		return m;
	}
	
	
	@Override
	public void pipeMsgEvent(PipeMsgEvent event) {
		
		Messages message = toMessages(event.getMessage());
		
		if(message.getMessage("type").equals("response")) {			
			super.pipeMsgEvent(event);
			return;
		}

		this.sendMessages(getResponseMessage(message), message.getMessage("source"));
	}

	
}
