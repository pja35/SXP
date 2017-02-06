package model.syncManager;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


import model.api.MessageSyncManager;
import model.entity.Message.ReceptionStatus;
import util.TestInputGenerator;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MessageSyncManagerImplTest {
	private final static Logger log = LogManager.getLogger(MessageSyncManagerImpl.class);
	private static MessageSyncManager msm;
	private static String id;
	private static String object = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(1, 129));
	private static Date sendingDate = TestInputGenerator.getTodayDate();;
	private static String senderId = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(1, 129));
	private static String senderName = TestInputGenerator.getRandomUser(20);
	private static Set<String> receiversIds = new HashSet<String>();
	private static Set<String> receiversNames = new HashSet<String>();
	private static String body = TestInputGenerator.getRandomIpsumString(TestInputGenerator.getRandomInt(1, 1025));
	public static ReceptionStatus status = ReceptionStatus.DRAFT;
	
	@BeforeClass
	public static void setUp() throws Exception {
	}

	@AfterClass
	public static void tearDown() throws Exception {
	}

	@Before
	public void initialize() throws Exception {
	}

	@After
	public void clean() throws Exception {
	}

	@Test
	public final void testA() {
		assertTrue(true); // TODO
	}

}
