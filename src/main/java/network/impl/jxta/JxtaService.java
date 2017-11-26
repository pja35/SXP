package network.impl.jxta;

import controller.tools.LoggerUtilities;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.ByteArrayMessageElement;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.Message.ElementIterator;
import net.jxta.endpoint.MessageElement;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.*;
import net.jxta.protocol.PipeAdvertisement;
import network.api.Messages;
import network.api.Peer;
import network.api.SearchListener;
import network.api.ServiceListener;
import network.api.advertisement.Advertisement;
import network.api.service.InvalidServiceException;
import network.api.service.Service;
import network.impl.MessagesGeneric;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This is the Jxta implementation of {@link Service}
 *
 * @author Julien Prudhomme
 * @see Peer
 */
public class JxtaService implements Service, DiscoveryListener, PipeMsgListener {
    @SuppressWarnings("unused")
    private final static Logger log = LogManager.getLogger(JxtaService.class);

    protected PeerGroup pg = null;
    protected String name;
    protected String peerUri = null;
    protected HashMap<String, ServiceListener> listeners = new HashMap<>();
    private SearchListener<Advertisement> currentSl;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void publishAdvertisement(Advertisement adv) {
        JxtaAdvertisement jxtaAdv = new JxtaAdvertisement(adv);
        try {
            pg.getDiscoveryService().publish(jxtaAdv.getJxtaAdvertisementBridge());
            pg.getDiscoveryService().remotePublish(jxtaAdv.getJxtaAdvertisementBridge());
        } catch (IOException e) {
            LoggerUtilities.logStackTrace(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws InvalidServiceException
     */
    @Override
    public void initAndStart(Peer peer) throws RuntimeException, InvalidServiceException {
        if (!(peer instanceof JxtaPeer)) {
            throw new RuntimeException("Need a Jxta Peer to run a Jxta service");
        }
        JxtaPeer jxtaPeer = (JxtaPeer) peer;
        jxtaPeer.addService(this);
        peerUri = peer.getUri();

        createInputPipe();
    }

    private void createInputPipe() {
        try {
            pg.getPipeService().createInputPipe(getAdvertisement(), this);
        } catch (IOException e) {
            LoggerUtilities.logStackTrace(e);
            throw new RuntimeException(e);
        }
    }

    protected void setPeerGroup(PeerGroup pg) {
        this.pg = pg;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void search(String attribute, String value, SearchListener<?> sl) {
        this.currentSl = (SearchListener<Advertisement>) sl;
        pg.getDiscoveryService().getRemoteAdvertisements(null, DiscoveryService.ADV, attribute, value, 10, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void discoveryEvent(DiscoveryEvent event) {
        Enumeration<net.jxta.document.Advertisement> advs = event.getResponse().getAdvertisements();
        ArrayList<Advertisement> advertisements = new ArrayList<>();
        while (advs.hasMoreElements()) {
            AdvertisementBridge adv = (AdvertisementBridge) advs.nextElement();
            Advertisement fadv = adv.getAdvertisement();
            fadv.setSourceURI("urn:jxta:" + event.getSource().toString().substring(7));

            advertisements.add(adv.getAdvertisement());
        }
        currentSl.notify(advertisements);
    }

    /**
     * Create a simple advertisement for the pipes' class.
     *
     * @return
     */
    protected PipeAdvertisement getAdvertisement() {
        return getPipeAdvertisement(IDFactory
                .newPipeID(pg.getPeerGroupID(), this.getClass().getName().getBytes()), false);
    }

    protected PipeAdvertisement getPipeAdvertisement(PipeID id, boolean is_multicast) {
        PipeAdvertisement adv = (PipeAdvertisement) AdvertisementFactory.
                newAdvertisement(PipeAdvertisement.getAdvertisementType());
        adv.setPipeID(id);
        if (is_multicast)
            adv.setType(PipeService.PropagateType);
        else
            adv.setType(PipeService.UnicastType);


        adv.setName("Pipe_" + this.getName());
        adv.setDescription("...");
        return adv;
    }

    protected Message toJxtaMessage(Messages m) {
        Message msg = new Message();

        for (String s : m.getNames()) {
            msg.addMessageElement(new ByteArrayMessageElement(s, null, m.getMessage(s).getBytes(), null));
        }

        //msg.addMessageElement(new ByteArrayMessageElement("WHO", null, m.getWho().getBytes(), null));
        return msg;
    }

    protected Messages toMessages(Message m) {
        MessagesGeneric msg = new MessagesGeneric();
        ElementIterator it = m.getMessageElements();
        while (it.hasNext()) {
            MessageElement e = it.next();
            if (e.getElementName().equals("WHO")) {
                msg.setWho(new String(e.getBytes(true)));
            } else {
                msg.addField(e.getElementName(), new String(e.getBytes(true)));
            }
        }
        return msg;
    }


    @Override
    public void sendMessages(Messages messages, String... uris) {

        Message message = toJxtaMessage(messages);
        HashSet<PeerID> to = new HashSet<PeerID>();
        for (String pidUri : uris) {
            try {
                PeerID pid = PeerID.create(new URI(pidUri));
                to.add(pid);
            } catch (URISyntaxException e) {
                LoggerUtilities.logStackTrace(e);
                throw new RuntimeException(e);
            }
        }
        try {
            OutputPipe pipe = pg.getPipeService().createOutputPipe(getAdvertisement(), to, 3000);
            pipe.send(message);
            pipe.close();
        } catch (IOException e) {
            LoggerUtilities.logStackTrace(e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public void pipeMsgEvent(PipeMsgEvent event) {
        Messages m = toMessages(event.getMessage());
        if (listeners.get(m.getWho()) != null) {
            listeners.get(m.getWho()).notify(m);
        }
    }


    @Override
    public void addListener(ServiceListener l, String who) {
        listeners.put(who, l);
    }

    @Override
    public void removeListener(String who) {
        listeners.remove(who);
    }

    @Override
    public void addAdvertisementListener(DiscoveryListener l) {
        pg.getDiscoveryService().addDiscoveryListener(l);
    }

    @Override
    public void removeAdvertisementListener(DiscoveryListener l) {
        pg.getDiscoveryService().removeDiscoveryListener(l);
    }

}
