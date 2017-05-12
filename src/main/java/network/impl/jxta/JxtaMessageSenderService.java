package network.impl.jxta;

import java.util.Collection;
import java.util.Hashtable;

import javax.print.attribute.standard.RequestingUserName;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import model.api.MessageSyncManager;
import model.entity.Message;
import model.factory.SyncManagerFactory;
import net.jxta.pipe.PipeMsgEvent;
import network.api.MessageRequestService;
import network.api.Messages;
import network.impl.MessagesGeneric;
import network.impl.messages.RequestMessageUserMessage;

public class JxtaMessageSenderService extends JxtaService implements MessageRequestService{
	
	public static final String NAME = "messagesSender";
	
	public JxtaMessageSenderService() {
		this.name = NAME;
	}
	
	@Override
	public void sendRequest(String senderId, String receiverId, String who, String ...uris) {
		
		System.out.println("JxtaMessageSenderService:sendRequest");
		
		RequestMessageUserMessage m = new RequestMessageUserMessage();
		
		m.setSenderId(senderId);
		
		m.setReceiverId(receiverId);
		
		m.setWho(who);
		
		m.setSource(this.peerUri);
		
		this.sendMessages(m, uris);
	}
	
	private Messages getResponseMessage(Messages msg) {
		System.out.println("JxtaMessageSenderService:getResponseMessage");
		MessagesGeneric m = new MessagesGeneric();
		
		m.setWho(msg.getWho());
		
		m.addField("type", "response");
	
		MessageSyncManager em = SyncManagerFactory.createMessageSyncManager();
		
		Collection<Message> messages =em.findAllByAttribute("receiverId", msg.getMessage("receiverId")); 
			
		//em.findAllByAttribute("senderId", msg.getMessage("senderId"));
		
		//messages.addAll(em.findAllByAttribute("receiverId", msg.getMessage("receiverId")));
		
		JsonTools<Collection<Message>> json = new JsonTools<>(new TypeReference<Collection<Message>>(){});
		
		m.addField("messages", json.toJson(messages));
		
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
