package model.entity;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;

@XmlRootElement
@Entity
public class Contract {
	@XmlElement(name="id")
	@UuidGenerator(name="uuid")
    @Id
    @GeneratedValue(generator="uuid")
	private String id;
	
	@XmlElement(name="title")
	private String title;
	
	@XmlElement(name="createdAt")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy")
	private Date createdAt;
	
	@XmlElement(name="clauses")
	@NotNull
	private ArrayList<String> clauses;
	
	@XmlElement(name="parties")
	@NotNull
	@Lob
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private ArrayList<String> parties;

	@XmlElement(name="signed")
	@NotNull
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private boolean signed;
	
	@XmlElement(name="userid")
	@NotNull
	private String userid;
	
	
	
	public String getId() {
		return id;
	}
	

	public String getTitle() {
		return title;
	}
	public void setTitle(String t) {
		this.title=t;
	}
	
	
	public void setCreatedAt(Date date) {
		createdAt = date;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}

	
	public ArrayList<String> getClauses() {
		return clauses;
	}
	public void setClauses(ArrayList<String> c) {
		this.clauses=c;
	}
	
	
	public ArrayList<String> getParties() {
		return parties;
	}
	public void setParties(ArrayList<String> p) {
		this.parties=p;
	}
	
	
	public boolean isSigned(){
		return signed;
	}
	public void setSigned(boolean s){
		this.signed = s;
	}
	
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String u) {
		this.userid=u;
	}
}
