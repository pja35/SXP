package controller;

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

import com.fasterxml.jackson.core.type.TypeReference;

import controller.tools.JsonTools;
import crypt.api.hashs.Hasher;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.factories.HasherFactory;
import model.api.SyncManager;
import model.api.UserSyncManager;
import model.entity.LoginToken;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import rest.api.Authentifier;
import rest.api.ServletPath;

@ServletPath("/api/users/*")
@Path("/")
public class Users {

	@GET
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public String login(
			@QueryParam("login") String login,
			@QueryParam("password") String password) {
		
		Authentifier auth = Application.getInstance().getAuth();
		UserSyncManager em = new UserSyncManagerImpl();
		User u = em.getUser(login, password);
		if(u != null) {
			LoginToken token = new LoginToken();
			token.setToken(auth.getToken(login, password));
			token.setUserid(u.getId());
			JsonTools<LoginToken> json = new JsonTools<>(new TypeReference<LoginToken>(){});
			return json.toJson(token);
		}
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
	
	@GET
	@Path("/subscribe")
	@Produces(MediaType.APPLICATION_JSON)
	public String subscribe(
			@QueryParam("login") String login,
			@QueryParam("password") String password) {
		
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
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String delete(
			@PathParam("id") long id) {
		return null;
	}
	
}
