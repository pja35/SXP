package controller;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.core.type.TypeReference;

import static org.junit.Assert.*;

import java.util.*;

import javax.net.ssl.HttpsURLConnection;

import controller.Application;
import controller.tools.JsonTools;
import controller.tools.LoggerUtilities;
import model.api.SyncManager;
import model.entity.Item;
import model.entity.LoginToken;
import model.entity.User;
import model.syncManager.ItemSyncManagerImpl;
import util.TestInputGenerator;
import util.TestUtils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.json.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import util.TrustModifier;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ControllerTest {
	private final static Logger log = LogManager.getLogger(ControllerTest.class);

	private static Application application;
	private static HttpsURLConnection https;
	private static final int restPort = 5600;
	private static final String baseURL = "https://localhost:" + String.valueOf(restPort) + "/";

	private static final String username = TestInputGenerator.getRandomAlphaWord(20);
	private static final String password = TestInputGenerator.getRandomPwd(20);

	private static String token;
	private static String userid;

	private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private static final int NbItems = 10;

	private static String itemTitle;
	private static String itemId;

	@BeforeClass
	static public void initialize() throws IOException{
		log.debug("**************** Starting test");
		application = new Application();
		application.runForTests(restPort);
		int loop = 0;
		int maxLoop = 30;
		while(!isJettyServerReady() && (loop < maxLoop)){
			loop++;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			}
		}
		if (loop == maxLoop){
			throw new IOException("Unable to connect Jetty Server.");
		}
	} 

	@AfterClass
	static public void deleteBaseAndPeer(){
		TestUtils.removeRecursively(new File(".db-" + restPort + "/"));
		TestUtils.removePeerCache();
		application.stop();
	}

	static private boolean isJettyServerReady(){
		boolean result = false;
		//HttpsURLConnection https;
		try {
			URL url = new URL(baseURL + "api/users/");

			https = (HttpsURLConnection)url.openConnection();
			TrustModifier.relaxHostChecking(https);
			https.setDoOutput(true);
			https.setDoInput(true);
			https.setRequestMethod("GET");
			result = (https.getResponseCode() == HttpsURLConnection.HTTP_OK);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			return false;
		}
		if(https != null){
			log.debug(TestUtils.get_https_cert(https));
			log.debug(TestUtils.get_https_content(https));
		}
		https.disconnect();
		return result;
	}

	private String connectAction(String method, String path, HashMap<String, String> properties, String data,
			boolean dataBin)
					throws IOException{
		//HttpsURLConnection 
		https = (HttpsURLConnection)new URL(baseURL + path).openConnection();
		try {
			TrustModifier.relaxHostChecking(https);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} 

		if (method.equals("POST") || method.equals("PUT") || method.equals("GET") || method.equals("DELETE"))
			https.setRequestMethod(method);
		else
			fail("Unknown http connection method : " + method);
		https.setDoInput(true);
		https.setDoOutput(true);
		https.setRequestProperty("Accept", "application/json");
		if(properties != null){
			for(String key : properties.keySet()){
				https.setRequestProperty(key, properties.get(key));
			}
		}
		if(data != null){
			if (dataBin){
				byte[] postData = data.getBytes( StandardCharsets.UTF_8 );
				https.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
				https.setRequestProperty( "charset", "utf-8");
				https.setRequestProperty( "Content-Length", Integer.toString( postData.length ));
				https.setUseCaches( false );
				DataOutputStream out = new DataOutputStream( https.getOutputStream());
				out.write( postData );
				out.flush();
				out.close();
			}else{
				https.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				https.setRequestProperty("Accept", "application/json, text/plain, */*");
				OutputStreamWriter out = new OutputStreamWriter(https.getOutputStream());
				out.write(data);
				out.flush();
				out.close();
			}
		}
		InputStream is = https.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader Ibr = new BufferedReader(isr);
		StringBuilder outputBuffer = new StringBuilder();
		String line; 
		while (( line = Ibr.readLine()) != null) { 
			outputBuffer.append(line + "\n");
		}
		String res = outputBuffer.toString();

		log.debug(res);
		https.disconnect();
		return res;
	}
	private String connectAction(String method, String path) throws IOException{
		return connectAction(method, path, null, null, false);
	}

	/**
	 * Unknown user login test.
	 */
	@Test
	public void testA(){
		try {
			String data = "login=foo"; 
			data += "&";
			data += "password=foo";
			JSONObject js = new JSONObject(connectAction("POST", "api/users/login/", null, data, true));
			assertTrue(js.get("error").equals("true"));
		} catch (Exception e) {			
			fail(LoggerUtilities.logStackTrace(e));
		}
	}

	/**
	 * Subscribe test.
	 */
	@Test
	public void testB(){
		try {
			String data = "login=" + username;
			data += "&";
			data += "password=" + password;
			JsonTools<LoginToken> json = new JsonTools<>(new TypeReference<LoginToken>(){});
			LoginToken lgt = json.toEntity(connectAction("POST", "api/users/subscribe", null, data, true));
			token = lgt.getToken();
			userid = lgt.getUserid();
			assertFalse(token.isEmpty());
			assertFalse(userid.isEmpty());
		} catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
		}
	}

	/**
	 * Logout and known user login test.
	 */
	@Test
	public void testC(){
		try {
			String data = "login=" + username; 
			data += "&";
			data += "password=" + password;
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			connectAction("GET", "api/users/logout", properties, null, true);
			JsonTools<LoginToken> json = new JsonTools<>(new TypeReference<LoginToken>(){});
			LoginToken lgt = json.toEntity(connectAction("POST", "api/users/login", null, data, true));
			assertFalse(lgt.getToken().isEmpty());
			assertFalse(lgt.getUserid().isEmpty());
			assertTrue(lgt.getUserid().equals(userid));
			token = lgt.getToken();
		} catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
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
			connectAction("GET", "api/users/logout", properties, null, true);
			JsonTools<User> json = new JsonTools<>(new TypeReference<User>(){});
			User usj = json.toEntity(connectAction("GET", "api/users/" + userid));
			log.debug("User Nick : " + usj.getNick());

		} catch (Exception e) {
			log.error("Associated bug : \"Unable to convert output of http request api/users/{id} into json object\"\n"
					+ e.getMessage());
			LoggerUtilities.logStackTrace(e);
			//fail(e.getMessage());
		}
	}

//	/**
//	 * Retriev all users
//	 */
//	@Test
//	public void testCb(){
//		try {			
//			JsonTools<Collection<User>> json = new JsonTools<>(new TypeReference<Collection<User>>(){});
//			Collection<User> uscoll = json.toEntity(connectAction("GET", "api/users/"));
//			assertTrue(uscoll.size() == 1);
//		} catch (Exception e) {
//			log.error("Associated bug : \"Unable to convert output of http request api/users/{id} into json object\"\n"
//					+ e.getMessage());
//			LoggerUtilities.logStackTrace(e);
//			//fail(e.getMessage());
//		}
//	}

	/**
	 * Get empty item test 
	 */
	@Test
	public void testD(){
		try {
			String data = "login=" + username; 
			data += "&";
			data += "password=" + password;
			JsonTools<LoginToken> lgtjs = new JsonTools<>(new TypeReference<LoginToken>(){});
			LoginToken lgt = lgtjs.toEntity(connectAction("POST", "api/users/login", null, data, true));
			token = lgt.getToken();
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			JsonTools<Collection<Item>> itjs = new JsonTools<>(new TypeReference<Collection<Item>>(){});
			Collection<Item> it = itjs.toEntity(connectAction("GET", "api/items", properties, null, true));
			assertTrue(it.isEmpty());
		} catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
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
				Item it = json.toEntity(connectAction("POST", "api/items", properties, data, false));
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
			fail(LoggerUtilities.logStackTrace(e));
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
			ArrayList<Item> it = (ArrayList<Item>)json.toEntity(connectAction("GET", "api/items", properties, null, true));
			assertTrue(it.size() == NbItems);
			int n = TestInputGenerator.getRandomInt(0, 10);
			itemId = it.get(n).getId();
			itemTitle = it.get(n).getTitle();
			log.debug(itemId + " --- " + itemTitle);
		} catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
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
			Item it = json.toEntity(connectAction("GET", "api/items/" + itemId, properties, null, true));
			String createdDate = dateFormat.format(it.getCreatedAt());
			assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
			assertTrue(it.getPbkey() != BigInteger.ZERO);
			assertTrue(it.getTitle().equals(itemTitle));
			assertTrue(it.getUserid().equals(userid));
			assertTrue(it.getUsername().equals(username));
		} catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
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
			Item it = json.toEntity(connectAction("PUT", "api/items/" + itemId, properties, data, false));
			String createdDate = dateFormat.format(it.getCreatedAt());
			assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
			assertTrue(it.getPbkey() != BigInteger.ZERO);			
			assertTrue(it.getTitle().equals(itemTitle));
			assertTrue(it.getUserid().equals(userid));
			assertTrue(it.getUsername().equals(username));
			assertTrue(it.getDescription().contains("Special"));
		} catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
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
			assertTrue("If this failed, verify that no XP server is running in background.", itcoll.size() == 1);
			Item it = itcoll.iterator().next();
			String createdDate = dateFormat.format(it.getCreatedAt());
			assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
			assertTrue(it.getPbkey() != BigInteger.ZERO);			
			assertTrue(it.getTitle().equals(itemTitle));
			assertTrue(it.getUserid().equals(userid));
			assertTrue(it.getUsername().equals(username));
			assertTrue(it.getDescription().contains("Special"));
			
		} catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
		} 
	}

	/**
	 * Delete all items
	 */
	@Test
	public void testK(){
		try {
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			JsonTools<Collection<Item>> json = new JsonTools<>(new TypeReference<Collection<Item>>(){});
			ArrayList<Item> itList = (ArrayList<Item>)json.toEntity(connectAction("GET", "api/items", properties, null, true));
			for(Item it : itList){
				JSONObject js = new JSONObject(connectAction("DELETE", "api/items/" + it.getId(), properties, null, true));
				assertTrue(js.get("deleted").equals("true"));
				log.debug("Item " + it.getTitle() + " is deleted.");
			}

		} catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
		}

	}

	/**
	 * Delete the user
	 */
	@Test
	public void testL(){
		try {
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			JSONObject js = new JSONObject(connectAction("DELETE", "api/users/" + userid, properties, null, true));
			assertTrue(js.get("deleted").equals("true"));
			log.debug("User " + username + " has been deleted.");
		} catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
		}

	}
}

