package rest.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import rest.api.Authentifier;
import rest.factories.AuthentifierFactory;
import util.TestInputGenerator;

/**
 * SimpleAuthentifier unit tests
 * @author denis.arrivault[@]univ-amu.fr
 */
public class SimpleAuthentifierTest {
	private final static Logger log = LogManager.getLogger(SimpleAuthentifier.class);
	AuthentifierFactory factory = new AuthentifierFactory();
	Authentifier authentifier;
	Map<String, List<String>> users;
	
	@Before
	public void initialize(){
		users = new HashMap<String, List<String>>();
		authentifier = AuthentifierFactory.createAuthentifier("simple");
		for(int i=0; i<TestInputGenerator.getRandomInt(10, 100); ++i){
			String username = TestInputGenerator.getRandomUser();
			String password = TestInputGenerator.getRandomPwd();
			String token = authentifier.getToken(username, password);
			List<String> ids = new ArrayList<String>();
			ids.add(username);
			ids.add(password);
			users.put(token, ids);
			log.debug("Adding [" + token  + "][" + username + "][" + password + "]");
			String tokenb = authentifier.getToken(username, password);
			users.put(tokenb, ids);
			log.debug("Adding [" + tokenb  + "][" + username + "][" + password + "]");
			assertFalse(tokenb.equals(token));
		}		
	}
	
	@Test
	public void idsGetterTest(){		
		for (Map.Entry<String, List<String>> entry : users.entrySet()) {
		    String token = entry.getKey();
		    List<String> ids = entry.getValue();
		    assertTrue(authentifier.getLogin(token).equals(ids.get(0)));
		    assertTrue(authentifier.getPassword(token).equals(ids.get(1)));
		    authentifier.deleteToken(token);
		    assertNull(authentifier.getLogin(token));
			assertNull(authentifier.getPassword(token));
		}
	}
}
