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

import model.api.EstablisherType;
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
	private ArrayList<String> clauses;
	
	// Id of the parties
	@XmlElement(name="parties")
	@Lob
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private ArrayList<String> parties;
	
	// Maps the id with the name
	@XmlElement(name="partiesNames")
	@Lob
	@JsonSerialize(using=controller.tools.MapSerializer.class)
	@JsonDeserialize(using=controller.tools.MapStringDeserializer.class)
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private HashMap<String,String> partiesNames;

	@XmlElement(name="wish")
	@NotNull
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private Wish wish;

	@XmlElement(name="status")
	@NotNull
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private Status status;

	@XmlElement(name="signatures")
	@JsonSerialize(using=controller.tools.MapSerializer.class)
	@JsonDeserialize(using=controller.tools.MapStringDeserializer.class)
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private HashMap<String, String> signatures;

	@XmlElement(name="establisherType")
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private EstablisherType establisherType;
	
	@XmlElement(name="establishementData")
	@Lob
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private String establishementData;
	
	
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
	
	
	public HashMap<String,String> getPartiesNames() {
		return partiesNames;
	}
	public void setPartiesNames(HashMap<String,String> n) {
		this.partiesNames=n;
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
	
	
	public EstablisherType getEstablisherType() {
		return establisherType;
	}
	public void setEstablisherType(EstablisherType e) {
		this.establisherType = e;
	}
	
	
	public String getEstablishementData() {
		return establishementData;
	}
	public void setEstablishementData(String e) {
		this.establishementData = e;
	}
}
