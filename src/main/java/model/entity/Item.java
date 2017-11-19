package model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import crypt.annotations.CryptSigneAnnotation;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigInteger;
import java.util.Date;


@XmlRootElement
@Entity
public class Item {

    @XmlElement(name = "id")
    @UuidGenerator(name = "uuid")
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    @XmlElement(name = "title")
    @NotNull
    @Size(min = 3, max = 255)
    private String title;

    @Lob
    @XmlElement(name = "description")
    @NotNull
    @Size(min = 3, max = 2000)
    private String description;

    @XmlElement(name = "createdAt")
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date createdAt;

    @XmlElement(name = "pbkey")
    @NotNull
    @Lob
    @JsonSerialize(using = controller.tools.BigIntegerSerializer.class)
    @JsonDeserialize(using = controller.tools.BigIntegerDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigInteger pbkey;

    @XmlElement(name = "username")
    @NotNull
    @Size(min = 2, max = 255)
    private String username;

    @XmlElement(name = "userid")
    @NotNull
    private String userid;


    @CryptSigneAnnotation(signeWithFields = {"title", "description", "createdAt", "username", "userid", "pbkey"}, checkByKey = "pbkey")
    //,ownerAttribute="userid")
    @XmlElement(name = "signature")
    //@NotNull
    private ElGamalSignEntity signature;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date date) {
        createdAt = date;
    }

    public BigInteger getPbkey() {
        return pbkey;
    }

    public void setPbkey(BigInteger pbkey) {
        this.pbkey = pbkey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


    public ElGamalSignEntity getSignature() {
        return signature;
    }

    public void setSignature(ElGamalSignEntity signature) {
        this.signature = signature;
    }
}
