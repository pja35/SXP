package model.entity;

import crypt.annotations.CryptHashAnnotation;
import crypt.annotations.CryptSigneAnnotation;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
@Entity
@Table(name = "\"User\"")
public class User {

    @XmlElement(name = "id")
    @UuidGenerator(name = "uuid")
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @XmlElement(name = "nick")
    @NotNull
    @Size(min = 3, max = 64)
    private String nick;

    @XmlElement(name = "salt")
    @NotNull
    private byte[] salt;

    @CryptHashAnnotation
    @XmlElement(name = "passwordHash")
    @NotNull
    private byte[] passwordHash;

    @Temporal(TemporalType.TIME)
    @NotNull
    @XmlElement(name = "createdAt")
    private Date createdAt;

    @NotNull
    @XmlElement(name = "keys")
    private ElGamalKey keys;

    @CryptSigneAnnotation(signeWithFields = {"nick", "createdAt", "passwordHash", "salt"}, checkByKey = "keys")
    //,ownerAttribute="id")
    @XmlElement(name = "signature")
//	@NotNull
    private ElGamalSignEntity signature;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    public ElGamalKey getKey() {
        return keys;
    }

    public void setKey(ElGamalKey key) {
        this.keys = key;
    }

    public ElGamalSignEntity getSignature() {
        return signature;
    }

    public void setSignature(ElGamalSignEntity signature) {
        this.signature = signature;
    }
}
