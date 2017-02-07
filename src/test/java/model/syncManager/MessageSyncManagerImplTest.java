package model.syncManager;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import model.api.MessageSyncManager;
import model.entity.Message;
import model.entity.Message.ReceptionStatus;
import model.factory.SyncManagerFactory;
import util.TestInputGenerator;
import util.TestUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageSyncManagerImplTest {
	private final static Logger log = LogManager.getLogger(MessageSyncManagerImpl.class);
	
	private static String id;
	private static String object = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(1, 129));
	private static Date sendingDate = TestInputGenerator.getTodayDate();;
	private static String senderId = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(1, 129));
	private static String senderName = TestInputGenerator.getRandomUser(20);
	private static List<String> receiversIds = new ArrayList<String>();
	private static List<String> receiversNames = new ArrayList<String>();
	private static String body = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(1, 1025));
	private static ReceptionStatus status;
	private static int nbReceivers = TestInputGenerator.getRandomInt(1, 100);
	private static String dbname = TestInputGenerator.getRandomDbName();

	private MessageSyncManager msm;
	private Message message; 

	@BeforeClass
	public static void setUp() throws Exception {
		Properties p = System.getProperties();
		p.put("derby.system.home", "./" + dbname + "/");
		for(int x=0; x<nbReceivers; ++x){
			receiversIds.add("#" + x);
			receiversNames.add(TestInputGenerator.getRandomUser());
		}
		int s = TestInputGenerator.getRandomInt(0, 3);
		switch(s){
			case 0:
				status = ReceptionStatus.DRAFT;
				break;
			case 1:
				status = ReceptionStatus.RECEIVED;
				break;
			default:
				status = ReceptionStatus.SENT;
				break;				
		}
	}

	@AfterClass
	public static void tearDown() throws Exception {
		clean();
		System.getProperties().put("derby.system.home", "./.simpleDb/");
	}

	@Before
	public void initialize() throws Exception {
		msm = SyncManagerFactory.createMessageSyncManager();
		message = new Message();
	}


	public static void clean() throws Exception {
		File db = new File(dbname);
		TestUtils.removeRecursively(db);
	}

	@Test
	public final void testA() {
		assertTrue(msm.begin());
		assertFalse(msm.persist(message)); 
		// javax.validation.ConstraintViolationException: Bean Validation constraint(s) violated while executing Automatic Bean Validation on callback event:'prePersist'. Please refer to embedded ConstraintViolations for details.
		assertFalse(msm.end());
	}


	@Test
	public final void testB() {
		message.setBody(body);
		message.setObject(object);
		message.setSendingDate(sendingDate);
		message.setSender(senderId, senderName);
		message.setStatus(status);
		for(int x=0; x<nbReceivers; ++x){
			message.addReceivers(receiversIds.get(x), receiversNames.get(x));
		}
		assertTrue(msm.begin());
		assertTrue(msm.persist(message));
		log.debug(dumpWL(msm));
		assertTrue(msm.contains(message));
		assertTrue(msm.end());
		assertFalse(msm.contains(message));	
	}
	
	@Test
	public final void testC() {
		Collection<Message> mess = msm.findAll();
		int x = 0;
		for(Message m : mess){
			id = m.getId();
			log.debug(x + " : " + m.getId() + " : " + m.getObject());
			assertTrue(m.getObject().equals(object));
			assertTrue(m.getBody().equals(body));
			x++;
		}
		assertTrue(x == 1);		
		Message m = msm.findOneById(id);
		assertTrue(m.getObject().equals(object));
		assertTrue(m.getBody().equals(body));
	}
	
	@Test
	public final void testD() {
		object = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(1, 129));
		body = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(1, 1025));
		message.setBody(body);
		message.setObject(object);
		message.setSendingDate(sendingDate);
		message.setSender(senderId, senderName);
		message.setStatus(status);
		for(int x=0; x<nbReceivers; ++x){
			message.addReceivers(receiversIds.get(x), receiversNames.get(x));
		}
		assertTrue(msm.begin());
		assertTrue(msm.persist(message));
		log.debug(dumpWL(msm));
		assertTrue(msm.check());
		message.setObject("");
		assertFalse(msm.check());
		message.setObject(object);
		assertTrue(msm.contains(message));
		assertTrue(msm.end());
	}


	public static String dumpWL(MessageSyncManager msm){
		StringBuffer buff = new StringBuffer();
		Set<Message> mss = (Set<Message>) msm.watchlist();
		buff.append("\n**** Watchlist ****" + "\n");
		for (Message m : mss){
			buff.append("------------" + "\n");
			buff.append(m.getId() + "\n");
			buff.append(m.getObject() + "\n");
			buff.append(m.getSenderId() + "\n");
			buff.append(m.getSendName() + "\n");
			buff.append(m.getSendingDate() + "\n");
			buff.append(m.getStatus() + "\n");
			buff.append(m.getBody() + "\n");
			for(int x=0; x<m.getReceiversIds().length; ++x){
				buff.append("Reciever .... " + m.getReceiversIds()[x] + "\n");
			}
		}
		buff.append("******************" + "\n");
		return buff.toString();
	}

}
