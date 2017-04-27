package protocol.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import controller.Application;
import crypt.api.hashs.Hasher;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.factories.HasherFactory;
import model.api.SyncManager;
import model.entity.ElGamalKey;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import protocol.impl.SigmaEstablisher;
import protocol.impl.sigma.SigmaClauses;
import protocol.impl.sigma.SigmaContract;
import protocol.impl.sigma.Trent;
import rest.api.Authentifier;
import util.TestInputGenerator;
import util.TestUtils;

public class SigmaEstablisherTest {
	public static Application application;
	public static final int restPort = 5600;
	
	public static final int N = 2;
	
	private static ElGamalKey trentK = ElGamalAsymKeyFactory.create(false);
	
	// Users logins and pwds
	private static String[] logins;
	private static String[] passwords;
	
	// Map of URIS
	private static HashMap<ElGamalKey, String> uris;
	

	// A contract for each signer
	private static SigmaContract[] c;

	
	@BeforeClass
	static public void initialize(){
		application = new Application();
		application.runForTests(restPort);
	}

	@AfterClass
	static public void deleteBaseAndPeer(){
		TestUtils.removeRecursively(new File(".db-" + restPort + "/"));
		TestUtils.removePeerCache();
		application.stop();
	}
	
	
	@Before
	public void instantiate(){
		// Initialize the users
		User[] u = new User[N];
		logins = new String[N];
		passwords = new String[N];
		uris = new HashMap<ElGamalKey, String>();
		c = new SigmaContract[N];
		
		for (int k=0; k<N; k++){
			logins[k] = TestInputGenerator.getRandomAlphaWord(20);
			passwords[k] = TestInputGenerator.getRandomPwd(20);
			
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
		}
		
		// Initialize the keys
		ElGamalKey[] keysR = new ElGamalKey[N];	
		String uri = Application.getInstance().getPeer().getUri();	
		for (int k=0; k<N; k++){
			ElGamalKey key = u[k].getKey();
			keysR[k] = new ElGamalKey();
			keysR[k].setG(key.getG());
			keysR[k].setP(key.getP());
			keysR[k].setPublicKey(key.getPublicKey());
			uris.put(keysR[k], uri);
		}
		uris.put(trentK, uri);
		
		// Initialize the contracts 
		ArrayList<String> cl = new ArrayList<String>();
		cl.add("clause 1");
		cl.add("second clause");
		SigmaClauses signable1 = new SigmaClauses(cl);
		

		ArrayList<ElGamalKey> parties = new ArrayList<ElGamalKey>();
		for(int i = 0; i<N; i++)
			parties.add(keysR[i]);
		
		for (int k=0; k<N; k++){
			c[k] = new SigmaContract(signable1);
			for (int i=0; i<N; i++){
				c[k].setParties(parties, true);
			}
		}
	}
	
	@Test
	public void goodSigningTest(){
		
		SigmaEstablisher[] sigmaE = new SigmaEstablisher[N];
		
		for (int k=0; k<N; k++){
			Authentifier auth = Application.getInstance().getAuth();
			sigmaE[k] = new SigmaEstablisher(auth.getToken(logins[k], passwords[k]), uris, trentK);
			sigmaE[k].initialize(c[k]);
		}
		
		// Time to setup the passwords
		try{
			Thread.sleep(1000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for (int k=0; k<N; k++)
			sigmaE[k].start();
		
		try{
			Thread.sleep(3001);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		boolean res = true;
		for (int k=0; k<N; k++){
			res =  res && c[k].isFinalized();
		}
		
		assertTrue(res);
	}

	
	@Test
	public void resolveTest(){
		// Failing round
		int limit = 1;
		
		new Trent(trentK);
		
		SigmaEstablisher[] sigmaE = new SigmaEstablisher[N];
		
		for (int k=1; k<N; k ++){
			Authentifier auth = Application.getInstance().getAuth();
			sigmaE[k] = new SigmaEstablisher(auth.getToken(logins[k], passwords[k]), uris, trentK);
			sigmaE[k].initialize(c[k]);
		}
		Authentifier auth = Application.getInstance().getAuth();
		sigmaE[0] = new SigmaEstablisherFailer(auth.getToken(logins[0], passwords[0]), uris, trentK, limit);
		sigmaE[0].initialize(c[0]);
		
		// Time to setup the passwords
		try{
			Thread.sleep(1000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int k=0; k<N; k++)
			sigmaE[k].start();
		
		// Time for listener to react
		try{
			Thread.sleep(3001);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
