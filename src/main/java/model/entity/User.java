package model.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.annotations.UuidGenerator;

@XmlRootElement
@Entity
@Table(name="\"User\"")
public class User {
	
	@XmlElement(name="id")
	@UuidGenerator(name="uuid")
    @Id
    @GeneratedValue(generator="uuid")
	private String id;
	
	@XmlElement(name="nick")
	@NotNull
	@Size(min = 3, max = 64)
	private String nick;
	
	@XmlElement(name="salt")
	@NotNull
	private byte[] salt;
	
	@XmlElement(name="passwordHash")
	@NotNull
	private byte[] passwordHash;
	
	@Temporal(TemporalType.TIME)
	@NotNull
	@XmlElement(name="createdAt")
	private Date createdAt;
	
	@NotNull
	@XmlElement(name="keys")
	private ElGamalKey keys;
	
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public String getNick() {
		return nick;
	}
	
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public Date getCreatedAt() {
		return createdAt;
	}
	
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}
	
	public byte[] getSalt() {
		return salt;
	}
	
	public void setPasswordHash(byte[] passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	public byte[] getPasswordHash() {
		return passwordHash;
	}

	public ElGamalKey getKey() {
		return keys;
	}

	public void setKey(ElGamalKey key) {
		this.keys = key;
	}
}
