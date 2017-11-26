package network.impl.messages;

import network.api.annotation.MessageElement;
import network.impl.MessagesImpl;

/**
 * @author Nathanaël EON
 */
public class EstablisherMessage extends MessagesImpl {
    @MessageElement("title")
    private String title;

    @MessageElement("sourceId")
    private String sourceId;

    // Contains the current Uri of the peer who sent the message
    @MessageElement("source")
    private String sourceUri;

    @MessageElement("type")
    private String type = "establisher";

    @MessageElement("contract")
    private String contract;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceI) {
        this.sourceId = sourceI;
    }

    public String getSource() {
        return sourceUri;
    }

    public void setSource(String source) {
        this.sourceUri = source;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String c) {
        this.contract = c;
    }
}
