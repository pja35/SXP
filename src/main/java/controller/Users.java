package controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.managers.CryptoItemManagerDecorator;
import controller.managers.CryptoUserManagerDecorator;
import controller.tools.JsonTools;
import crypt.api.hashs.Hasher;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.factories.HasherFactory;
import model.api.ManagerListener;
import model.api.SyncManager;
import model.api.UserSyncManager;
import model.entity.ElGamalSignEntity;
import model.entity.Item;
import model.entity.LoginToken;
import model.entity.User;
import model.manager.ManagerAdapter;
import model.syncManager.ItemSyncManagerImpl;
import model.syncManager.UserSyncManagerImpl;
import rest.api.Authentifier;
import rest.api.ServletPath;

@ServletPath("/api/users/*")
@Path("/")
public class Users {
	private final static Logger log = LogManager.getLogger(Users.class);
	
	//@GET
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)	
	public String login(String jsonCredentials) {
		String[] credentials = jsonCredentials.split("&");
		String login = credentials[0].split("=")[1];
		String password = credentials[1].split("=")[1];
		/*	public String login(
			@QueryParam("login") String login,
			@QueryParam("password") String password) {*/

		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager em = new UserSyncManagerImpl();
		User u = em.getUser(login, password);
		log.info(login + " - " + password);
		if(u != null) {
			LoginToken token = new LoginToken();
			token.setToken(auth.getToken(login, password));
			token.setUserid(u.getId());
			JsonTools<LoginToken> json = new JsonTools<>(new TypeReference<LoginToken>(){});
			em.close();
			return json.toJson(token);
		}
		em.close();
		return "{\"error\": \"true\"}";
		/*EntityManager<User> em = new UserManager();
		User u = em.findOneByAttribute("nick", login);
		if(u == null) return "{\"error\": \"true\"}";
		System.out.println("user trouve !");
		Hasher hasher = HasherFactory.createDefaultHasher();
		hasher.setSalt(u.getSalt());
		//check if passwords are the sames
		String hash1 = new String(u.getPasswordHash());
		String hash2 = new String(hasher.getHash(password.getBytes()));

		if(hash1.equals(hash2)) {
			LoginToken token = new LoginToken();
			token.setToken(auth.getToken(login, password));
			token.setUserid(u.getId());
			JsonTools<LoginToken> json = new JsonTools<>();
			json.initialize(LoginToken.class);
			return json.toJson(token);
		}

		return "{\"error\": \"true\"}";*/
	}

	@GET
	@Path("/logout")
	public String logout(@HeaderParam(Authentifier.PARAM_NAME) String token) {
		Authentifier auth = Application.getInstance().getAuth();
		auth.deleteToken(token);
		return null;
	}

	//@GET
	@POST
	@Path("/subscribe")
	@Produces(MediaType.APPLICATION_JSON)
	public String subscribe(String jsonCredentials) {
		String[] credentials = jsonCredentials.split("&");
		String login = credentials[0].split("=")[1];
		String password = credentials[1].split("=")[1];
		/*public String subscribe(
			@QueryParam("login") String login,
			@QueryParam("password") String password) {*/

		User u = new User();
		u.setNick(login);
		u.setSalt(HasherFactory.generateSalt());
		u.setPasswordHash(password.getBytes());
		u.setCreatedAt(new Date());
		u.setKey(ElGamalAsymKeyFactory.create(false));
		u.setSignature(new ElGamalSignEntity());
		//SyncManager<User> em = new UserSyncManagerImpl();
		
		ManagerAdapter<User> adapter = new ManagerAdapter<User>(new UserSyncManagerImpl());
		
		CryptoUserManagerDecorator hasherDecoratorUser = new CryptoUserManagerDecorator(adapter,u);
		
		hasherDecoratorUser.begin();
		hasherDecoratorUser.persist(u);
		hasherDecoratorUser.end();
		hasherDecoratorUser.close();
		
		Authentifier auth = Application.getInstance().getAuth();
		LoginToken token = new LoginToken();
		token.setToken(auth.getToken(login, password));
		token.setUserid(u.getId());
		JsonTools<LoginToken> json = new JsonTools<>(new TypeReference<LoginToken>(){});
		return json.toJson(token);
	}

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String add(User user) {

		return null;
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String get(
			@PathParam("id") String id) {
		SyncManager<User> em = new UserSyncManagerImpl();
		JsonTools<User> json = new JsonTools<>(new TypeReference<User>(){});
		return json.toJson(em.findOneById(id));
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String get() {
		SyncManager<User> em = new UserSyncManagerImpl();
		JsonTools<Collection<User>> json = new JsonTools<>(new TypeReference<Collection<User>>(){});
		return json.toJson(em.findAll());
		//return JsonUtils.collectionStringify(em.findAll());
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String edit(User user) {

		return null;
	}

	@POST
	@Path("/password")
	@Produces(MediaType.APPLICATION_JSON)
	public String changePassword(@HeaderParam(Authentifier.PARAM_NAME) String token, String jsonCredentials) {
		
		String[] credentials = jsonCredentials.split("&");
		String passwordOld = credentials[0].split("=")[1];
		String passwordNew = credentials[1].split("=")[1];
		String passwordNewConfirm = credentials[2].split("=")[1];
		
		
		if(!passwordNew.equals(passwordNewConfirm)){
			return "{\"error\": \"true\"}";
		}
		
		Authentifier auth = Application.getInstance().getAuth();
		
		UserSyncManager em = new UserSyncManagerImpl();
		
		User u = em.getUser(auth.getLogin(token), passwordOld);
		
		ManagerAdapter<User> adapter = new ManagerAdapter<User>(em);
		CryptoUserManagerDecorator decoratorUserMg = new CryptoUserManagerDecorator(adapter,u);
		
		if(u != null) {
			
			decoratorUserMg.begin();
			
			LoginToken newToken = new LoginToken();
			newToken.setToken(auth.getToken(u.getNick(), passwordNew));
			newToken.setUserid(u.getId());		

			Hasher hasher = HasherFactory.createDefaultHasher();
			u.setSalt(HasherFactory.generateSalt());
			u.setPasswordHash(passwordNew.getBytes());
			
			if (decoratorUserMg.end()){
				decoratorUserMg.close();
				JsonTools<LoginToken> json = new JsonTools<>(new TypeReference<LoginToken>(){});
				return json.toJson(newToken);
			}
		}
		decoratorUserMg.close();
		return null;
	}

	/** 
	 * This only deletes users from local base.
	 * TO DO : connect to jxta
	 * @param id
	 * @param token
	 * @return
	 */
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(@PathParam("id") String id, @HeaderParam(Authentifier.PARAM_NAME) String token) {
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager users = new UserSyncManagerImpl();
		User currentUser = users.getUser(auth.getLogin(token), auth.getPassword(token));
		if (currentUser == null){
			users.close();
			return "{\"deleted\": \"false\"}";
		}
		Boolean ret = users.begin();
		User us = users.findOneById(id);
		return "{\"deleted\": \"" + (ret && users.remove(us) && users.end() && users.close()) + "\"}";		
	}
	
}
