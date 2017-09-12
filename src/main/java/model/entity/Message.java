package model.entity;

import static javax.persistence.EnumType.STRING;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.annotations.UuidGenerator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import crypt.annotations.CryptCryptAnnotation;
import crypt.annotations.CryptSigneAnnotation;

@XmlRootElement
@Entity
public class Message {

	@XmlElement(name="id")
	@UuidGenerator(name="uuid")
	@Id
	@GeneratedValue(generator="uuid")
	private String id;

	@XmlElement(name="sendingDate")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy HH:mm:ss") 
	private Date sendingDate;

	@XmlElement(name="senderId")
	@NotNull
	@Size(min = 1, max = 128)
	private String senderId;

	@XmlElement(name="senderName")
	@NotNull
	@Size(min = 1, max = 128)
	private String senderName;

	@XmlElement(name="receiverId")
	@NotNull
	private String receiverId;

	@XmlElement(name="receiverName")
	@NotNull
	private String receiverName;
	
	@CryptCryptAnnotation(isCryptBySecondKey=true,secondKey="pbkey")
	@Lob
	@XmlElement(name="messageContent")
	@NotNull
	@Size(min = 1, max = 4096)
	private String messageContent;

	public static enum ReceptionStatus {DRAFT, SENT, RECEIVED}
	@XmlElement(name="status")
	@Enumerated(STRING)
	public ReceptionStatus status = ReceptionStatus.DRAFT;   
	
	
	@XmlElement(name="pbkey")
	@NotNull
	@Lob
	@JsonSerialize(using=controller.tools.BigIntegerSerializer.class)
	@JsonDeserialize(using=controller.tools.BigIntegerDeserializer.class)
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private BigInteger pbkey;
	
	@Lob
	@CryptSigneAnnotation(signeWithFields={"sendingDate","senderId","senderName","receiverId","receiverName","pbkey","messageContent"},checkByKey="pbkey") //,ownerAttribute="senderId")
	@XmlElement(name="signature")
	//@NotNull
	private ElGamalSignEntity signature;
	
	
	public String getId(){
		return this.id;
	}

	public void setSendingDate(Date date){
		this.sendingDate = date;
	}

	public Date getSendingDate(){
		return this.sendingDate;
	}

	public void setSender(String id, String name){
		this.senderId = id;
		this.senderName = name;
	}

	public String getSenderId(){
		return this.senderId;
	}
	public String getSenderName(){
		return this.senderName;
	}

	public void setReceiver(String id, String name){
		this.receiverId = id;
		this.receiverName = name;
	}

	public String getReceiverId(){
		return this.receiverId;
	}

	public String getReceiverName(){
		return this.receiverName;
	}


	public void setMessageContent(String content){
		this.messageContent = content;
	}

	public String getMessageContent(){
		return this.messageContent;
	}

	public void setStatus(ReceptionStatus status){
		this.status = status;
	}

	public ReceptionStatus getStatus(){
		return this.status;
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

}
