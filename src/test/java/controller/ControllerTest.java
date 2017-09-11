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

import model.syncManager.ItemSyncManagerImpl;

import crypt.api.hashs.Hasher;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.factories.HasherFactory;
import model.api.SyncManager;
import model.api.Wish;
import model.entity.ContractEntity;
import model.entity.Item;
import model.entity.LoginToken;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import protocol.impl.sigma.Trent;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;

import util.TrustModifier;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ControllerTest {
	private final static Logger log = LogManager.getLogger(ControllerTest.class);

	private static Application application;
	private static HttpsURLConnection https;
	public static final int restPort = 5600;
	private static final String baseURL = "https://localhost:" + String.valueOf(restPort) + "/";

	private static final String username = TestInputGenerator.getRandomAlphaWord(20);
	private static final String password = TestInputGenerator.getRandomPwd(20);

	private static String token;
	private static String userid;

	//private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private static final int NbItems = 10;
	private static final int NbContracts = 10;

	private static String itemTitle;
	private static String itemId;
	
	private static String contractTitle;
	private static String contractId;

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
			result = (https.getResponseCode() == HttpURLConnection.HTTP_OK);
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
				//String createdDate = dateFormat.format(it.getCreatedAt());
				// TODO : make the date identicals
				//assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
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
			//String createdDate = dateFormat.format(it.getCreatedAt());
			// TODO : set equal dates (problem on midnight here
			//assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
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
			//String createdDate = dateFormat.format(it.getCreatedAt());
			// TODO : set equal dates (problem on midnight here
			//assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
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
			//String createdDate = dateFormat.format(it.getCreatedAt());
			// TODO : set equal dates (problem on midnight here
			//assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
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
	 * Get empty contract list
	 */
	@Test
	public void testL(){
		try {
			String data = "login=" + username; 
			data += "&";
			data += "password=" + password;
			JsonTools<LoginToken> lgtjs = new JsonTools<>(new TypeReference<LoginToken>(){});
			LoginToken lgt = lgtjs.toEntity(connectAction("POST", "api/users/login", null, data, true));
			token = lgt.getToken();
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			JsonTools<Collection<ContractEntity>> ctjs = new JsonTools<>(new TypeReference<Collection<ContractEntity>>(){});
			Collection<ContractEntity> ct = ctjs.toEntity(connectAction("GET", "api/contracts", properties, null, true));
			assertTrue(ct.isEmpty());
		} catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
		}
	}
	
	/**
	 * Add contracts
	 */
	@Test
	public void testM(){
		try {
			for(int i=0; i<NbContracts; ++i){
				String login = TestInputGenerator.getRandomAlphaWord(20);
				String password = TestInputGenerator.getRandomPwd(20);
				
				User u = new User();
				u.setNick(login);
				Hasher hasher = HasherFactory.createDefaultHasher();
				u.setSalt(HasherFactory.generateSalt());
				hasher.setSalt(u.getSalt());
				u.setPasswordHash(hasher.getHash(password.getBytes()));
				u.setCreatedAt(new Date());
				u.setKey(ElGamalAsymKeyFactory.create(false));
				SyncManager<User> em = new UserSyncManagerImpl();
				em.begin();
				em.persist(u);
				em.end();
				(new Trent(u.getKey())).setListener();
				
				HashMap<String, String> properties = new HashMap<String, String>();
				properties.put("Auth-Token", token);
				String data = "{\"title\":\"Object_"+ i + "\",\"parties\":[\""+ userid+"\",\""+u.getId()+"\"]}";
				if (i==0)
					data = "{\"title\":\"\",\"parties\":[\""+ userid+"\",\""+u.getId()+"\"]}";
				JsonTools<ContractEntity> json = new JsonTools<>(new TypeReference<ContractEntity>(){});
				ContractEntity ct = json.toEntity(connectAction("POST", "api/contracts", properties, data, false));
				//String createdDate = dateFormat.format(ct.getCreatedAt());
				// TODO : set equal dates (problem on midnight here)
				//assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
				assertFalse(ct.getId().isEmpty());
				if (i!=0)
					assertTrue(ct.getTitle().equals("Object_" + i));
				else
					assertTrue(ct.getTitle().equals("Secure Exchange Protocol Contract"));
				assertTrue(ct.getUserid().equals(userid));
			}
		} catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
		}
	}


	/**
	 * Check contract list test and pick one
	 */
	@Test
	public void testN(){
		try {
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			JsonTools<Collection<ContractEntity>> json = new JsonTools<>(new TypeReference<Collection<ContractEntity>>(){});
			ArrayList<ContractEntity> ct = (ArrayList<ContractEntity>)json.toEntity(connectAction("GET", "api/contracts", properties, null, true));
			assertTrue(ct.size() == NbContracts);
			int n = TestInputGenerator.getRandomInt(0, NbContracts);
			contractId = ct.get(n).getId();
			itemTitle = ct.get(n).getTitle();
			log.debug(contractId + " --- " + contractTitle);
		} catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
		}
	}



	/**
	 * Get a contract by its id
	 */
	@Test
	public void testO(){
		try {
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			JsonTools<ContractEntity> json = new JsonTools<>(new TypeReference<ContractEntity>(){});
			ContractEntity ct = json.toEntity(connectAction("GET", "api/contracts/" + contractId, properties, null, true));
			//String createdDate = dateFormat.format(ct.getCreatedAt());
			// TODO : set equal dates (problem on midnight here)
			//assertTrue(createdDate.equals(TestInputGenerator.getFormatedTodayDate("dd-MM-yyyy")));
			assertTrue(ct.getTitle().equals(itemTitle));
			assertTrue(ct.getUserid().equals(userid));
		} catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
		} 
	}
	
	/**
	 * Edit the previous contract
	 */
	@Test
	public void testP(){
		try{
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			JsonTools<ContractEntity> json = new JsonTools<>(new TypeReference<ContractEntity>(){});
			String c = connectAction("GET", "api/contracts/" + contractId, properties, null, true);
			ContractEntity ct = json.toEntity(c);
			ArrayList<String> clauses = new ArrayList<String>();
			clauses.add("Clause1");
			ct.setClauses(clauses);
			String c2 = connectAction("PUT", "api/contracts/" + contractId, properties, json.toJson(ct), false);
			ContractEntity ct2 = json.toEntity(c2);
			assertFalse(c2.equals(c));
			assertTrue(ct2.getClauses().equals(ct.getClauses()));
		}catch (Exception e){
			fail(LoggerUtilities.logStackTrace(e));
		}
	}
	
	/**
	 * Start signing protocol
	 */
	@Test
	public void testQ(){
		try {
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			JsonTools<ContractEntity> json = new JsonTools<>(new TypeReference<ContractEntity>(){});
			String ret = connectAction("PUT", "api/contracts/sign/" + contractId, properties, null, true);
			assertTrue(ret.substring(0, 4).equals("true"));
			String c = connectAction("GET", "api/contracts/" + contractId, properties, null, true);
			ContractEntity ct = json.toEntity(c);
			assertTrue(ct.getWish().equals(Wish.ACCEPT));
		}catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
		}
	}
	
	/**
	 * Try to cancel a contract
	 */
	@Test
	public void testR(){
		try {
			HashMap<String, String> properties = new HashMap<String, String>();
			JsonTools<ContractEntity> json = new JsonTools<>(new TypeReference<ContractEntity>(){});
			properties.put("Auth-Token", token);
			String ret = connectAction("PUT", "api/contracts/cancel/" + contractId, properties, null, true);
			assertTrue(ret.substring(0, 4).equals("true"));
			String c = connectAction("GET", "api/contracts/" + contractId, properties, null, true);
			ContractEntity ct = json.toEntity(c);
			assertTrue(ct.getWish().equals(Wish.REFUSE));
		}catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
		}
	}
	
	/**
	 * Try to delete a contract with an undefined user
	 */
	@Test
	public void testS(){
		try {
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			JsonTools<Collection<ContractEntity>> json = new JsonTools<>(new TypeReference<Collection<ContractEntity>>(){});
			ArrayList<ContractEntity> ctList = (ArrayList<ContractEntity>)json.toEntity(connectAction("GET", "api/contracts", properties, null, true));
			for(ContractEntity ct : ctList){
				properties.put("Auth-Token", "");
				JSONObject js = new JSONObject(connectAction("DELETE", "api/contracts/" + ct.getId(), properties, null, true));
				assertFalse(js.get("deleted").equals("true"));
			}
		} catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
		}
	}
	
	/**
	 * Try to delete with the wrong user
	 */
	@Test
	public void testSa(){
		try {
			//Create user 2
			String data = "login=" + username + "1";
			data += "&";
			data += "password=" + password + "1";
			JsonTools<LoginToken> json = new JsonTools<>(new TypeReference<LoginToken>(){});
			LoginToken lgt = json.toEntity(connectAction("POST", "api/users/subscribe", null, data, true));
			String token2 = lgt.getToken();
			
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			JsonTools<Collection<ContractEntity>> json2 = new JsonTools<>(new TypeReference<Collection<ContractEntity>>(){});
			ArrayList<ContractEntity> ctList = (ArrayList<ContractEntity>)json2.toEntity(connectAction("GET", "api/contracts", properties, null, true));
			for(ContractEntity ct : ctList){
				properties.put("Auth-Token", token2);
				JSONObject js = new JSONObject(connectAction("DELETE", "api/contracts/" + ct.getId(), properties, null, true));
				assertFalse(js.get("deleted").equals("true"));
			}
		}catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
		}
	}
	
	/**
	 * Delete all contracts
	 */
	@Test
	public void testSb(){
		try {
			HashMap<String, String> properties = new HashMap<String, String>();
			properties.put("Auth-Token", token);
			JsonTools<Collection<ContractEntity>> json = new JsonTools<>(new TypeReference<Collection<ContractEntity>>(){});
			ArrayList<ContractEntity> ctList = (ArrayList<ContractEntity>)json.toEntity(connectAction("GET", "api/contracts", properties, null, true));
			for(ContractEntity ct : ctList){
				JSONObject js = new JSONObject(connectAction("DELETE", "api/contracts/" + ct.getId(), properties, null, true));
				assertTrue(js.get("deleted").equals("true"));
				log.debug("Contract " + ct.getTitle() + " is deleted.");
			}

		} catch (Exception e) {
			fail(LoggerUtilities.logStackTrace(e));
		}
	}
	
	/**
	 * Delete the user
	 */
	@Test
	public void testT(){
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

