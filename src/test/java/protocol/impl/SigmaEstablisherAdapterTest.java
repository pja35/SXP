package protocol.impl;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.junit.Test;

import controller.Application;
import crypt.api.hashs.Hasher;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.factories.HasherFactory;
import model.api.SyncManager;
import model.entity.ElGamalKey;
import model.entity.LoginToken;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import protocol.api.Wish;
import protocol.impl.contract.ElGamalClauses;
import protocol.impl.contract.ElGamalContract;
import rest.api.Authentifier;

public class SigmaEstablisherAdapterTest {
	
	public static final int N = 2;
		
	@Test
	public void test(){		
		// Starting the Application (if needed) to be able to test it
		if (Application.getInstance()==null){
			new Application();
			Application.getInstance().runForTests(8081);
		}
		
		// Creating the users
		User[] u = new User[N];
		String[] logins = new String[N];
		String[] passwords = new String[N];
		for (int k=0; k<N; k++){
			logins[k] = createString(5);
			passwords[k] = createString(10);
			
			u[k] = new User();
			u[k].setNick(logins[k]);
			Hasher hasher = HasherFactory.createDefaultHasher();
			u[k].setSalt(HasherFactory.generateSalt());
			hasher.setSalt(u[k].getSalt());
			u[k].setPasswordHash(hasher.getHash(passwords[k].getBytes()));
			u[k].setCreatedAt(new Date());
			u[k].setKey(ElGamalAsymKeyFactory.create(false));
			SyncManager<User> em = new UserSyncManagerImpl();
			em.begin();
			em.persist(u[k]);
			em.end();

			Authentifier auth = Application.getInstance().getAuth();
			LoginToken token = new LoginToken();
			token.setToken(auth.getToken(logins[k], passwords[k]));
			token.setUserid(u[k].getId());
		}
		
		//Initialize the keys
		ElGamalKey[] keysR = new ElGamalKey[N];
		// Creating the contracts 
		ElGamalClauses signable1 = new ElGamalClauses("Hi");
		ElGamalContract[] c = new ElGamalContract[N];
		
		// Creating the map of URIS
		String uri = Application.getInstance().getPeer().getUri();
		HashMap<ElGamalKey, String> uris = new HashMap<ElGamalKey, String>();
		

		for (int k=0; k<N; k++){
			ElGamalKey key = u[k].getKey();
			keysR[k] = new ElGamalKey();
			keysR[k].setG(key.getG());
			keysR[k].setP(key.getP());
			keysR[k].setPublicKey(key.getPublicKey());
			
			uris.put(keysR[k], uri);
		}
		
		for (int k=0; k<N; k++){
			c[k] = new ElGamalContract();
			c[k].setClauses(signable1);
			for (int i=0; i<N; i++){
				c[k].addParty(keysR[i]);
			}
		}
		SigmaEstablisherAdapter[] sigmaE = new SigmaEstablisherAdapter[N];
		
		for (int k=0; k<N; k++){
			Authentifier auth = Application.getInstance().getAuth();
			sigmaE[k] = new SigmaEstablisherAdapter(auth.getToken(logins[k], passwords[k]), uris);
			sigmaE[k].initialize(c[k]);
		}
		
		// Time to setup the passwords
		try{
			Thread.sleep(1000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for (int k=0; k<N; k++)
			sigmaE[k].setWish(Wish.ACCEPT);
		
		try{
			Thread.sleep(3001);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}

		boolean res = true;
		for (int k=0; k<N; k++){
			System.out.println(k + " : " +c[k].isFinalized()); 
			res =  res && c[k].isFinalized();
		}
		
		assertTrue(res);
	}
	private String createString(int len){
		// Characters we will use to encrypt
		char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?".toCharArray();
		
		// Build a random String from the characters
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int j = 0; j < len; j++) {
		    char c = characters[random.nextInt(characters.length)];
		    sb.append(c);
		}
		return sb.toString();
	}

}
