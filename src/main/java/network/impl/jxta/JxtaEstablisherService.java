/**
 * 
 */
package network.impl.jxta;

import net.jxta.pipe.PipeMsgEvent;
import network.api.EstablisherService;
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
	public EstablisherMessage sendContract(String title, String who, String sourceId, String contract, String... peerURIs) 
	{
		EstablisherMessage m = new EstablisherMessage();
		m.setTitle(title);
		m.setWho(who);
		m.setSourceId(sourceId);
		m.setSource(this.peerUri);
		m.setContract(contract);
		this.sendMessages(m, peerURIs);
		return m;
	}
	
	/**
	 * Method called when a message is caught in the pipe
	 */
	@Override
	public void pipeMsgEvent(PipeMsgEvent event) {
		System.out.println("ADVERTISEMENT ?");
		super.pipeMsgEvent(event);
	}

}
