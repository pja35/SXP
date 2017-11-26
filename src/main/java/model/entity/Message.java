package model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import crypt.annotations.CryptCryptAnnotation;
import crypt.annotations.CryptSigneAnnotation;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.util.*;

import static javax.persistence.EnumType.STRING;

@XmlRootElement
@Entity
public class Message {



    @XmlElement(name = "ContractTitle")
    private String ContractTitle;


    @XmlElement(name = "contractID")
    private String contractID;


    @XmlElement(name = "chatID")
    private String chatID;


    @XmlElement(name = "status")
    @Enumerated(STRING)
    public ReceptionStatus status = ReceptionStatus.DRAFT;
    @XmlElement(name = "id")
    @UuidGenerator(name = "uuid")
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;
    @XmlElement(name = "sendingDate")
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private Date sendingDate;
    @XmlElement(name = "senderId")
    @NotNull
    @Size(min = 1, max = 128)
    private String senderId;
    @XmlElement(name = "senderName")
    @NotNull
    @Size(min = 1, max = 128)
    private String senderName;

    @XmlElement(name = "receiverId")
    @NotNull
    private String receiverId;

    @XmlElement(name = "receiverName")
    @NotNull
    private String receiverName;

    @XmlElement(name = "receivers")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ArrayList<String> receivers;



    @XmlElement(name = "receiversNicks")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ArrayList<String> receiversNicks;



    @CryptCryptAnnotation(isCryptBySecondKey = true, secondKey = "pbkey")
    @Lob
    @XmlElement(name = "messageContent")
    @NotNull
    @Size(min = 1, max = 4096)
    private String messageContent;
    @XmlElement(name = "pbkey")
    @NotNull
    @Lob
    @JsonSerialize(using = controller.tools.BigIntegerSerializer.class)
    @JsonDeserialize(using = controller.tools.BigIntegerDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigInteger pbkey;
    @Lob
    @CryptSigneAnnotation(signeWithFields = {"sendingDate", "senderId", "senderName", "receiverId", "receiverName", "pbkey", "messageContent"}, checkByKey = "pbkey")
    //,ownerAttribute="senderId")
    @XmlElement(name = "signature")
    //@NotNull
    private ElGamalSignEntity signature;


    public ArrayList<String> getReceiversNicks() {
        return receiversNicks;
    }

    public void setReceiversNicks(ArrayList<String> receiversNicks) {
        this.receiversNicks = receiversNicks;
    }

    public String getChatID(){
        return this.chatID;
    }

    public void setChatID(String uuid){
        this.chatID = uuid;
    }

    public String getContractID(){
        return this.contractID;
    }

    public String getContractTitle() {
        return this.ContractTitle;
    }

    public void setContractTitle(String ContractTitle) {
        this.ContractTitle = ContractTitle;
    }

    public void setContractID(String contractID) {
        this.contractID = contractID;
    }

    public String getId() {
        return this.id;
    }

    public Date getSendingDate() {
        return this.sendingDate;
    }

    public void setReceiver(String id, String name) {
        this.receiverId = id;
        this.receiverName = name;
    }

    public String getReceiverId() {
        return this.receiverId;
    }

    public String getReceiverName() {
        return this.receiverName;
    }

    public void setSendingDate(Date date) {
        this.sendingDate = date;
    }

    public void setSender(String id, String name) {
        this.senderId = id;
        this.senderName = name;
    }

    public String getSenderId() {
        return this.senderId;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public void setReceivers(ArrayList<String> receivers) {
       this.receivers = receivers;
    }

    public ArrayList<String> getReceivers() {
        return this.receivers;
    }

    public String getMessageContent() {
        return this.messageContent;
    }

    public void setMessageContent(String content) {
        this.messageContent = content;
    }

    public ReceptionStatus getStatus() {
        return this.status;
    }

    public void setStatus(ReceptionStatus status) {
        this.status = status;
    }

    public BigInteger getPbkey() {
        return pbkey;
    }

    public void setPbkey(BigInteger pbkey) {
        this.pbkey = pbkey;
    }

    public ElGamalSignEntity getSignature() {
        return signature;
    }

    public void setSignature(ElGamalSignEntity signature) {
        this.signature = signature;
    }



    public static enum ReceptionStatus {DRAFT, SENT, RECEIVED}

}
