package model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import static javax.persistence.EnumType.STRING;
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
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy hh:mm:ss")
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

	@Lob
	@XmlElement(name="messageContent")
	@NotNull
	@Size(min = 1, max = 1024)
	private String messageContent;

	public static enum ReceptionStatus {DRAFT, SENT, RECEIVED}
	@XmlElement(name="status")
	@Enumerated(STRING)
	public ReceptionStatus status = ReceptionStatus.DRAFT;   

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
	public String getSendName(){
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

	/**
	 * @return a complete string with all attributes (mainly used for debug)
	 */
	public String getString(){
		StringBuffer buff = new StringBuffer();		
		buff.append("******************" + "\n");
		buff.append("Message Id = " + getId() + "\n");
		buff.append("Sender Id = " + getSenderId() + "\n");
		buff.append("Sender Name = " + getSendName() + "\n");
		buff.append("Sending date = " + getSendingDate() + "\n");
		buff.append(getStatus() + "\n");
		buff.append(getMessageContent() + "\n");
		buff.append("Receiver Id = " + getReceiverId() + "\n");
		buff.append("Receiver name = " + getReceiverName() + "\n");
		buff.append("******************" + "\n");
		return buff.toString();
	}

}
