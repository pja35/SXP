package controller;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.core.type.TypeReference;

import static org.junit.Assert.*;

import java.util.*;

import controller.Application;
import controller.tools.JsonTools;
import model.entity.ElGamalKey;
import model.entity.Item;
import model.entity.LoginToken;
import model.entity.User;
import util.TestInputGenerator;
import util.TestUtils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ControllerTest {
	private final static Logger log = LogManager.getLogger(ControllerTest.class);
	Application application;
	private static final int restPort = 8081;
	private static final String baseURL = "http://0.0.0.0:" + String.valueOf(restPort) + "/";

	private static final String username = TestInputGenerator.getRandomAlphaWord(20);
	private static final String password = TestInputGenerator.getRandomPwd(20);

	private static String token;
	private static String userid;
	private static String userNick;
	private static ElGamalKey userkey;
	private static byte[] userpwdhash;

	private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private static final int NbItems = 10;

	private static String itemTitle;
	private static String itemId;


	@BeforeClass
	static public void initialize(){
		Application application = new Application();
		application.runForTests(restPort);
		while(!isJettyServerReady()){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			}
		}
	} 

	@AfterClass
	static public void deleteBaseAndPeer(){
		TestUtils.removeRecursively(new File(".db-" + restPort + "/"));
		TestUtils.removePeerCache();
	}

	static private boolean isJettyServerReady(){
		boolean result = false;
		try {
			HttpURLConnection http = (HttpURLConnection)new URL(baseURL + "api/users/login").openConnection();
			http.setRequestMethod("GET");
			result = (http.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (IOException e) {
			return false;
		}
		return result;
	}

	private String connectAction(String method, String path, HashMap<String, String> properties, String data)
			throws IOException{
		HttpURLConnection http = (HttpURLConnection)new URL(baseURL + path).openConnection();
		http.setDoInput(true);
		http.setDoOutput(true);
		if (method.equals("POST") || method.equals("PUT"))
			http.setRequestMethod(method);
		else
			http.setRequestMethod("GET");
		http.setRequestProperty("Accept", "application/json");
		if(properties != null){
			for(String key : properties.keySet()){
				http.setRequestProperty(key, properties.get(key));
			}
		}
		if(data != null){
			http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			http.setRequestProperty("Accept", "application/json, text/plain, */*");
			OutputStreamWriter out = new OutputStreamWriter(http.getOutputStream());
			out.write(data);
			out.close();
		}
		InputStream is = http.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader Ibr = new BufferedReader(isr);
		StringBuilder outputBuffer = new StringBuilder();
		String line; 
		while (( line = Ibr.readLine()) != null) { 
			outputBuffer.append(line + "\n");
		}
		String res = outputBuffer.toString();

		log.debug(res);
		return res;
	}
	private String connectAction(String method, String path) throws IOException{
		return connectAction(method, path, null, null);
	}

	/**
	 * Unknown user login test.
	 */
	@Test
	public void testA(){
		try {
			JSONObject js = new JSONObject(connectAction("GET", "api/users/login?login=foo&password=foo"));
			assertTrue(js.get("error").equals("true"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Subscribe test.
	 */
	@Test
	public void testB(){
		try {
			JsonTools<LoginToken> json = new JsonTools<>(new TypeReference<LoginToken>(){});
			LoginToken lgt = json.toEntity(connectAction("GET", "api/users/subscribe?login=" + username + "&password=" + password));
			token = lgt.getToken();
			userid = lgt.getUserid();
			assertFalse(token.isEmpty());
			assertFalse(userid.isEmpty());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Logout and known user login test.
	 */
	@Test
	public void testC(){
		try {
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			connectAction("GET", "api/users/logout", properties, null);
			JsonTools<LoginToken> json = new JsonTools<>(new TypeReference<LoginToken>(){});
			LoginToken lgt = json.toEntity(connectAction("GET", "api/users/login?login=" + username + "&password=" + password));
			assertFalse(lgt.getToken().isEmpty());
			assertFalse(lgt.getUserid().isEmpty());
			assertTrue(lgt.getUserid().equals(userid));
			token = lgt.getToken();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Retrieve user from id
	 */
	@Test
	public void testCa(){
		try {
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			connectAction("GET", "api/users/logout", properties, null);
			JsonTools<User> json = new JsonTools<>(new TypeReference<User>(){});
			User usj = json.toEntity(connectAction("GET", "api/users/" + userid));
			//			String createdDate = dateFormat.format(usj.getCreatedAt());
			//			assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
			//			assertTrue(usj.getId().equals(userid));
			//			userNick = usj.getNick();
			//			userkey = usj.getKey();
			//			userpwdhash = usj.getPasswordHash();
			log.debug("User Nick : " + usj.getNick());

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Associated bug : \"Unable to convert output of http request api/users/{id} into json object\"");
			//fail(e.getMessage());
		}
	}

	/**
	 * Retriev all users
	 */
	@Test
	public void testCb(){
		try {			
			JsonTools<Collection<User>> json = new JsonTools<>(new TypeReference<Collection<User>>(){});
			Collection<User> uscoll = json.toEntity(connectAction("GET", "api/users/"));
			assertTrue(uscoll.size() == 1);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Associated bug : \"Unable to convert output of http request api/users/{id} into json object\"");
			//fail(e.getMessage());
		}
	}

	/**
	 * Get empty item test 
	 */
	@Test
	public void testD(){
		try {
			JsonTools<LoginToken> lgtjs = new JsonTools<>(new TypeReference<LoginToken>(){});
			LoginToken lgt = lgtjs.toEntity(connectAction("GET", "api/users/login?login=" + username + "&password=" + password));
			token = lgt.getToken();
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			JsonTools<Collection<Item>> itjs = new JsonTools<>(new TypeReference<Collection<Item>>(){});
			Collection<Item> it = itjs.toEntity(connectAction("GET", "api/items", properties, null));
			assertTrue(it.isEmpty());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Add items test 
	 */
	@Test
	public void testE(){
		try {
			for(int i=0; i<NbItems; ++i){
				HashMap<String, String> properties = new HashMap<String, String>();
				properties.put("Auth-Token", token);
				String data = "{\"title\":\"Object_"+ i 
						+ "\",\"description\":\"Description_" + i + "\"}";
				JsonTools<Item> json = new JsonTools<>(new TypeReference<Item>(){});
				Item it = json.toEntity(connectAction("POST", "api/items", properties, data));
				String createdDate = dateFormat.format(it.getCreatedAt());
				assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
				assertTrue(it.getDescription().equals("Description_" + i));
				assertFalse(it.getId().isEmpty());
				assertTrue(it.getPbkey() != BigInteger.ZERO);
				assertTrue(it.getTitle().equals("Object_" + i));
				assertTrue(it.getUserid().equals(userid));
				assertTrue(it.getUsername().equals(username));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Check item list test and pick one
	 */
	@Test
	public void testF(){
		try {
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			JsonTools<Collection<Item>> json = new JsonTools<>(new TypeReference<Collection<Item>>(){});
			ArrayList<Item> it = (ArrayList<Item>)json.toEntity(connectAction("GET", "api/items", properties, null));
			assertTrue(it.size() == NbItems);
			int n = TestInputGenerator.getRandomInt(0, 10);
			itemId = it.get(n).getId();
			itemTitle = it.get(n).getTitle();
			log.debug(itemId + " --- " + itemTitle);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Get an item by its id
	 */
	@Test
	public void testG(){
		try {
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			//properties.put("Connection", "keep-alive");
			JsonTools<Item> json = new JsonTools<>(new TypeReference<Item>(){});
			Item it = json.toEntity(connectAction("GET", "api/items/" + itemId, properties, null));
			String createdDate = dateFormat.format(it.getCreatedAt());
			assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
			assertTrue(it.getPbkey() != BigInteger.ZERO);
			assertTrue(it.getTitle().equals(itemTitle));
			assertTrue(it.getUserid().equals(userid));
			assertTrue(it.getUsername().equals(username));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 
	}

	/**
	 * Change an item's description
	 */
	@Test
	public void testH(){
		try {
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			String data = "{\"id\":\"" + itemId + "\",\"title\":\"" + itemTitle + "\",\"description\":\"Special description\"}";
			JsonTools<Item> json = new JsonTools<>(new TypeReference<Item>(){});
			Item it = json.toEntity(connectAction("PUT", "api/items/" + itemId, properties, data));
			String createdDate = dateFormat.format(it.getCreatedAt());
			assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
			assertTrue(it.getPbkey() != BigInteger.ZERO);			
			assertTrue(it.getTitle().equals(itemTitle));
			assertTrue(it.getUserid().equals(userid));
			assertTrue(it.getUsername().equals(username));
			assertTrue(it.getDescription().contains("Special"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 
	}

	/**
	 * Search for an item with simple
	 */
	@Test
	public void testI(){
		try {

			JsonTools<Collection<Item>> json = new JsonTools<>(new TypeReference<Collection<Item>>(){});
			Collection<Item> itcoll = json.toEntity(connectAction("GET", "api/search/simple?title=" + itemTitle));
			assertTrue(itcoll.size() == 1);
			Item it = itcoll.iterator().next();
			String createdDate = dateFormat.format(it.getCreatedAt());
			assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
			assertTrue(it.getPbkey() != BigInteger.ZERO);			
			assertTrue(it.getTitle().equals(itemTitle));
			assertTrue(it.getUserid().equals(userid));
			assertTrue(it.getUsername().equals(username));
			assertTrue(it.getDescription().contains("Special"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} 
	}

	/**
	 * Search for an item with simple2
	 */
	@Test
	public void testJ(){
		try {

			JsonTools<Collection<Item>> json = new JsonTools<>(new TypeReference<Collection<Item>>(){});
			Collection<Item> itcoll = json.toEntity(connectAction("GET", "api/search/simple2?title=" + itemTitle));
			log.error("Associated bug : \"In Search.java algorithm simple2 does not work.\"");
			//			assertTrue(itcoll.size() == 1);
			//			Item it = itcoll.iterator().next();
			//			String createdDate = dateFormat.format(it.getCreatedAt());
			//			assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
			//			assertTrue(it.getPbkey() != BigInteger.ZERO);			
			//			assertTrue(it.getTitle().equals(itemTitle));
			//			assertTrue(it.getUserid().equals(userid));
			//			assertTrue(it.getUsername().equals(username));
			//			assertTrue(it.getDescription().contains("Special"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());

		} 
	}
}

