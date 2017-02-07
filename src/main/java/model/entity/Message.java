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

	@XmlElement(name="object")
	@NotNull
	@Size(min = 1, max = 128)
	private String object;

	@XmlElement(name="sendingDate")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy")
	private Date sendingDate;

	@XmlElement(name="senderId")
	@NotNull
	@Size(min = 1, max = 128)
	private String senderId;

	@XmlElement(name="senderName")
	@NotNull
	@Size(min = 1, max = 128)
	private String senderName;

	@XmlElement(name="receiversIds")
	@NotNull
	@ElementCollection
	private Set<String> receiversIds = new HashSet<String>();

	@XmlElement(name="receiversNames")
	@NotNull
	@ElementCollection
	private Set<String> receiversNames = new HashSet<String>();

	@Lob
	@XmlElement(name="body")
	@NotNull
	@Size(min = 1, max = 1024)
	private String body;

	public static enum ReceptionStatus {DRAFT, SENT, RECEIVED}
	@XmlElement(name="status")
	@Enumerated(STRING)
	public ReceptionStatus status;   

	public String getId(){
		return this.id;
	}

	public void setObject (String object){
		this.object = object;
	}

	public String getObject(){
		return this.object;
	}

	public void setSendingDate (Date date){
		this.sendingDate = date;
	}

	public Date getSendingDate(){
		return this.sendingDate;
	}

	public void setSender (String id, String name){
		this.senderId = id;
		this.senderName = name;
	}

	public String getSenderId(){
		return this.senderId;
	}
	public String getSendName(){
		return this.senderName;
	}

	public void addReceivers (String id, String name){
		this.receiversIds.add(id);
		this.receiversNames.add(name);
	}

	public int sizeOfReceivers(){
		return this.receiversIds.size();
	}

	public String[] getReceiversIds(){
		String[] ids = new String[this.receiversIds.size()];
		return this.receiversIds.toArray(ids);
	}

	public void setBody (String body){
		this.body = body;
	}

	public String getBody(){
		return this.body;
	}

	public void setStatus(ReceptionStatus status){
		this.status = status;
	}

	public ReceptionStatus getStatus(){
		return this.status;
	}

}
