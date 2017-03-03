package model.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import model.api.Status;
import model.api.Wish;

@XmlRootElement
@Entity
public class ContractEntity {
	@XmlElement(name="id")
	@UuidGenerator(name="uuid")
    @Id
    @GeneratedValue(generator="uuid")
	private String id;
	
	@XmlElement(name="userid")
	@NotNull
	private String userid;
	
	@XmlElement(name="createdAt")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd-MM-yyyy")
	private Date createdAt;
	
	@XmlElement(name="title")
	private String title;
	
	@XmlElement(name="clauses")
	@NotNull
	private ArrayList<String> clauses;
	
	@XmlElement(name="parties")
	@NotNull
	@Lob
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private ArrayList<String> parties;

	@XmlElement(name="wish")
	@NotNull
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private Wish wish;

	@XmlElement(name="status")
	@NotNull
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private Status status;

	@XmlElement(name="signatures")
	@JsonSerialize(using=controller.tools.MapStringSerializer.class)
	@JsonDeserialize(using=controller.tools.MapStringDeserializer.class)
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private HashMap<String, String> signatures;
	
	
	
	public String getId() {
		return id;
	}
	
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String u) {
		this.userid=u;
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
	
	
	public Wish getWish() {
		return wish;
	}
	public void setWish(Wish w) {
		this.wish=w;
	}
	
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status s) {
		this.status=s;
	}
	
	
	public HashMap<String,String> getSignatures() {
		return signatures;
	}
	public void setSignatures(HashMap<String,String> s) {
		this.signatures=s;
	}
}
