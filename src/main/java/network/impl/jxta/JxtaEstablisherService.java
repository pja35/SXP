/**
 * 
 */
package network.impl.jxta;

import net.jxta.pipe.PipeMsgEvent;
import network.api.EstablisherService;
import network.api.Messages;
import network.impl.messages.EstablisherMessage;

/**
 * @author Nathanaël EON
 *
 */
public class JxtaEstablisherService extends JxtaService implements EstablisherService{
	public static final String NAME = "establisher";
	
	public JxtaEstablisherService ()
	{
		this.name = NAME;
	}
	
	
	@Override
	public EstablisherMessage sendPromI(String title, String who, String promI, String... peerURIs) 
	{
		EstablisherMessage m = new EstablisherMessage();
		m.setTitle(title);
		m.setWho(who);
		m.setSource(this.peerUri);
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
	}

}
