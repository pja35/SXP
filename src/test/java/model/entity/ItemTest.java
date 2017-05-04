package model.entity;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.persistence.annotations.UuidGenerator;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import util.TestInputGenerator;

/**
 * Item unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class ItemTest {
	@SuppressWarnings("unused")
	private final static Logger log = LogManager.getLogger(ItemTest.class);
	Item item;
	String title;
	String description;
	Date date;
	BigInteger publicKey;
	String username;
	String userid;

	@Before
	public void instantiate(){
		item = new Item();
		title = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(3, 256));
		description = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(3, 2001));
		date = TestInputGenerator.getTodayDate();
		publicKey = TestInputGenerator.getRandomBigInteger(TestInputGenerator.getRandomInt(3, 256));
		username = TestInputGenerator.getRandomUser(100); 
		userid = TestInputGenerator.getRandomUser();
		item.setTitle(title);
		item.setDescription(description);
		item.setCreatedAt(date);
		item.setPbkey(publicKey);
		item.setUsername(username);
		item.setUserid(userid);		
	}

	@Test
	public void fieldAnnotationsTest(){
		//Id
		try{
			Field idField = item.getClass().getDeclaredField("id");
			assertTrue(idField.getAnnotation(XmlElement.class).name().equals("id"));
			assertTrue(idField.getAnnotation(UuidGenerator.class).name().equals("uuid"));
			assertTrue(idField.getAnnotation(Id.class) != null);
			assertTrue(idField.getAnnotation(GeneratedValue.class).generator().equals("uuid"));
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		//title
		try{
			Field titleField = item.getClass().getDeclaredField("title");
			assertTrue(titleField.getAnnotation(XmlElement.class).name().equals("title"));
			assertTrue(titleField.getAnnotation(NotNull.class) != null);
			assertTrue(titleField.getAnnotation(Size.class).min() == 3);
			assertTrue(titleField.getAnnotation(Size.class).max() == 255);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		//description
		try{
			Field descField = item.getClass().getDeclaredField("description");
			assertTrue(descField.getAnnotation(XmlElement.class).name().equals("description"));
			assertTrue(descField.getAnnotation(NotNull.class) != null);
			assertTrue(descField.getAnnotation(Size.class).min() == 3);
			assertTrue(descField.getAnnotation(Size.class).max() == 2000);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		//createdAt
		try{
			Field creatField = item.getClass().getDeclaredField("createdAt");
			assertTrue(creatField.getAnnotation(XmlElement.class).name().equals("createdAt"));
			assertTrue(creatField.getAnnotation(NotNull.class) != null);
			assertTrue(creatField.getAnnotation(Temporal.class).value().compareTo(TemporalType.TIMESTAMP) == 0);
			assertTrue(creatField.getAnnotation(JsonFormat.class).shape().compareTo(JsonFormat.Shape.STRING) == 0);
			assertTrue(creatField.getAnnotation(JsonFormat.class).pattern().equals("dd-MM-yyyy"));
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		//pbkey
		try{
			Field pbKeyField = item.getClass().getDeclaredField("pbkey");
			assertTrue(pbKeyField.getAnnotation(XmlElement.class).name().equals("pbkey"));
			assertTrue(pbKeyField.getAnnotation(NotNull.class) != null);
			assertTrue(pbKeyField.getAnnotation(Lob.class) != null);
			assertTrue(pbKeyField.getAnnotation(JsonSerialize.class).using().equals(controller.tools.BigIntegerSerializer.class));
			assertTrue(pbKeyField.getAnnotation(JsonDeserialize.class).using().equals(controller.tools.BigIntegerDeserializer.class));
			assertTrue(pbKeyField.getAnnotation(JsonFormat.class).shape().compareTo(JsonFormat.Shape.STRING) == 0);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		//username
		try{
			Field unField = item.getClass().getDeclaredField("username");
			assertTrue(unField.getAnnotation(XmlElement.class).name().equals("username"));
			assertTrue(unField.getAnnotation(NotNull.class) != null);
			assertTrue(unField.getAnnotation(Size.class).min() == 2);
			assertTrue(unField.getAnnotation(Size.class).max() == 255);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		//userid
		try{
			Field uidField = item.getClass().getDeclaredField("userid");
			assertTrue(uidField.getAnnotation(XmlElement.class).name().equals("userid"));
			assertTrue(uidField.getAnnotation(NotNull.class) != null);
		}catch(Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void classAnnotationsTest(){
		assertTrue(item.getClass().getAnnotation(Entity.class) != null);
		assertTrue(item.getClass().getAnnotation(XmlRootElement.class) != null);
	}

	@Test
	public void gettersTest() {
		assertTrue(item.getId() == null);
		assertTrue(item.getTitle().equals(title));
		assertTrue(item.getDescription().equals(description));
		assertTrue(item.getCreatedAt().equals(date));		
		assertTrue(item.getPbkey().equals(publicKey));		
		assertTrue(item.getUsername().equals(username));		
		assertTrue(item.getUserid().equals(userid));		
	}
}
