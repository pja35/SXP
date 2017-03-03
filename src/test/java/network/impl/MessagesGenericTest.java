package network.impl;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import util.TestInputGenerator;

/**
 * MessagesGenericTest unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class MessagesGenericTest {
	MessagesGeneric msgs;
	HashMap<String, String> fields;
	String Who;
	
	@Before
	public void initialize(){
		msgs = new MessagesGeneric();
		fields = new HashMap<String, String>();
		for(int i=0; i<TestInputGenerator.getRandomInt(10, 100); ++i){
			String name = TestInputGenerator.getRandomUser();
			String value = TestInputGenerator.getRandomIpsumText();
			fields.put(name, value);
			msgs.addField(name, value);
		}
	}
	
	@Test
	public void getterTest() {
		String[] names = msgs.getNames();
		for (String name : names)
			assertTrue(fields.get(name).equals(msgs.getMessage(name)));
		Who = TestInputGenerator.getRandomUser();
		msgs.setWho(Who);
		assertTrue(msgs.getWho().equals(Who));
	}
}
