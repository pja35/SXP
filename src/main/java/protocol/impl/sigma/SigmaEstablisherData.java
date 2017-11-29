package protocol.impl.sigma;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import controller.tools.MapKeyStringDeserializer;
import controller.tools.MapSerializer;
import model.entity.ElGamalKey;
import model.entity.sigma.Or;
import protocol.impl.sigma.steps.ProtocolStep;

import javax.xml.bind.annotation.XmlElement;
import java.util.HashMap;

public class SigmaEstablisherData {

    @XmlElement(name = "uris")
    @JsonSerialize(using = MapSerializer.class)
    @JsonDeserialize(using = MapKeyStringDeserializer.class)
    protected HashMap<ElGamalKey, String> uris;
    @XmlElement(name = "trentkey")
    private ElGamalKey trentKey;
    @XmlElement(name = "protocolstep")
    private ProtocolStep protocolStep;
    @XmlElement(name = "roundReceived")
    private Or[][] roundReceived;
    @XmlElement(name = "contract")
    private SigmaContract contract;

    public ElGamalKey getTrentKey() {
        return this.trentKey;
    }

    public void setTrentKey(ElGamalKey t) {
        this.trentKey = t;
    }

    public ProtocolStep getProtocolStep() {
        return this.protocolStep;
    }

    public void setProtocolStep(ProtocolStep p) {
        this.protocolStep = p;
    }

    public Or[][] getRoundReceived() {
        return this.roundReceived;
    }

    public void setRoundReceived(Or[][] r) {
        this.roundReceived = r;
    }

    public SigmaContract getContract() {
        return this.contract;
    }

    public void setContract(SigmaContract s) {
        this.contract = s;
    }

    public HashMap<ElGamalKey, String> getUris() {
        return this.uris;
    }

    public void setUris(HashMap<ElGamalKey, String> u) {
        this.uris = u;
    }
}
