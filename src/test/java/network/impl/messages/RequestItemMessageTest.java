package network.impl.messages;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import util.TestInputGenerator;

/**
 * RequestItemMessage unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class RequestItemMessageTest {
	private final static Logger log = LogManager.getLogger(RequestItemMessageTest.class);
	@Rule public ExpectedException exception = ExpectedException.none();
	RequestItemMessage msgs;
	HashMap<String, String> fields;
	
	@Before
	public void initialize(){
		msgs = new RequestItemMessage();
		fields = new HashMap<String, String>();
		fields.put("source", TestInputGenerator.getRandomIpsumString(200));
		fields.put("title", TestInputGenerator.getRandomIpsumString(200));
		fields.put("WHO", TestInputGenerator.getRandomUser());
		fields.put("type", "request");
		msgs.setSource(fields.get("source"));
		msgs.setTitle(fields.get("title"));
		msgs.setWho(fields.get("WHO"));
	}
	
	@Test
	public void getterTest() {
		assertTrue(msgs.getSource().equals(fields.get("source")));
		assertTrue(msgs.getTitle().equals(fields.get("title")));
		assertTrue(msgs.getWho().equals(fields.get("WHO")));
	}
	
	@Test
	public void testFieldExceptions() {
		exception.expect(RuntimeException.class);
		exception.expectMessage("field doesn't exist !");		
		msgs.getMessage("foo");
	}

	@Test
	public void annotationTest() {
		String[] names = msgs.getNames();
		for (String name : names){
			assertTrue(msgs.getMessage(name).equals(fields.get(name)));
			log.debug("[" + name + "]" + " = " + msgs.getMessage(name));
		}
	}
}
