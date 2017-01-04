/**
 * 
 */
package network.impl.jxta;

import net.jxta.pipe.PipeMsgEvent;
import network.api.EstablisherService;
import network.api.Messages;
import network.impl.messages.EstablisherMessage;

/**
 * @author soriano
 *
 */
public class JxtaEstablisherService extends JxtaService implements EstablisherService 
{
	public static final String NAME = "establisher";
	
	public JxtaEstablisherService ()
	{
		this.name = NAME;
	}
	
	/*private Messages getResponseMessage(Messages msg) {
		MessagesGeneric m = new MessagesGeneric();
		m.addField("type", "response");
		m.setWho(msg.getWho());
		return m;
	}*/
	
	@Override
	public EstablisherMessage sendPromI(String title, String whoId, String whoUid, String promI, String... peerURIs) 
	{
		EstablisherMessage m = new EstablisherMessage();
		m.setTitle(title);
		m.setWho(whoId);
		m.setReceiver(whoUid);
		m.setPromI(promI);
		this.sendMessages(m, peerURIs);
		return m;
	}
	
	@Override
	public void pipeMsgEvent(PipeMsgEvent event) {
		Messages message = toMessages(event.getMessage());
		if(message.getMessage("type").equals("establisher")) {
			super.pipeMsgEvent(event);
			return;
		}
		super.pipeMsgEvent(event);
		//this.sendMessages(getResponseMessage(message), message.getMessage("source"));
		
	}

}
