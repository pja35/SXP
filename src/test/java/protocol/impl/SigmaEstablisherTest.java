package protocol.impl;


import static org.junit.Assert.assertFalse;
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
import model.api.Status;
import model.api.SyncManager;
import model.entity.ContractEntity;
import model.entity.ElGamalKey;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import protocol.impl.sigma.SigmaContract;
import protocol.impl.sigma.Trent;
import util.TestInputGenerator;
import util.TestUtils;

public class SigmaEstablisherTest {
	public static final boolean useMessages = false;
	public static final int N = 2;
	
	public static Application application;
	public static final int restPort = 5601;
	
	// Users
	private static User[] u;
	private static ElGamalKey[] keysR;
	private static ElGamalKey trentK = ElGamalAsymKeyFactory.create(false);
	private static Trent trent;
	// Map of URIS
	private static HashMap<ElGamalKey, String> uris;

	// A contract for each signer
	private static SigmaContract[] c;
	// A contract entity
	private static ContractEntity[] ce;
	
	@BeforeClass
	public static void setup(){
		application = new Application();
		application.runForTests(restPort);
		trent = new Trent(trentK);
		trent.setListener();
	}


	@AfterClass
	public static void stopApp(){
		TestUtils.removeRecursively(new File(".db-" + restPort + "/"));
		TestUtils.removePeerCache();
		application.stop();
	}
	
	
	/*
	 * Create the users, the application
	 */
	@Before
	public void initialize(){		
		// Initialize the users
		u = new User[N];
		for (int k=0; k<N; k++){
			String login = TestInputGenerator.getRandomAlphaWord(20);
			String password = TestInputGenerator.getRandomPwd(20);
			
			u[k] = new User();
			u[k].setNick(login);
			Hasher hasher = HasherFactory.createDefaultHasher();
			u[k].setSalt(HasherFactory.generateSalt());
			hasher.setSalt(u[k].getSalt());
			u[k].setPasswordHash(hasher.getHash(password.getBytes()));
			u[k].setCreatedAt(new Date());
			u[k].setKey(ElGamalAsymKeyFactory.create(false));
			SyncManager<User> em = new UserSyncManagerImpl();
			em.begin();
			em.persist(u[k]);
			em.end();
		}
		
		// Initialize the keys
		ElGamalKey key;
		keysR = new ElGamalKey[N];	
		for (int k=0; k<N; k++){
			key = u[k].getKey();
			keysR[k] = new ElGamalKey();
			keysR[k].setG(key.getG());
			keysR[k].setP(key.getP());
			keysR[k].setPublicKey(key.getPublicKey());
		}
		
		if (useMessages){
			uris = new HashMap<ElGamalKey, String>();	
			String uri = Application.getInstance().getPeer().getUri();
			for (int k=0; k<N; k++)
				uris.put(keysR[k], uri);
		}
		
		c = new SigmaContract[N];
		ce = new ContractEntity[N];
		
		// Initialize the contracts 
		ArrayList<String> cl = new ArrayList<String>();
		cl.add(TestInputGenerator.getRandomIpsumText());
		cl.add(TestInputGenerator.getRandomIpsumText());
		

		ArrayList<String> parties = new ArrayList<String>();
		for(int i = 0; i<u.length; i++){
			parties.add(u[i].getId());
		}

		for (int k=0; k<N; k++){
			ce[k] = new ContractEntity();
			ce[k].setParties(parties);
			ce[k].setClauses(cl);
			ce[k].setSignatures(new HashMap<String, String>());
			c[k] = new SigmaContract(ce[k]);
		}
	}
	
	
	// Test a simple signing protocol
	@Test
	public void TestA(){
		SigmaEstablisher[] sigmaE = new SigmaEstablisher[N];
		for (int k=0; k<N; k++){
				sigmaE[k] = new SigmaEstablisher(u[k].getKey(), trentK, uris);
		}
		
		for (int k=0; k<N; k++){
			sigmaE[k].initialize(c[k]);
		}
		
		// Time to setup listeners
		try{
			Thread.sleep(100);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		for (int k=0; k<N; k++){
			sigmaE[k].start();
		}
		
		// Time to realize procedure
		for (int k=0; k<3; k++){
			try{
				Thread.sleep(1000);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		boolean res = true;
		for (int k=0; k<N; k++){
			res =  res && c[k].isFinalized();
		}
		
		assertTrue(res);
		
		for (int k=0; k<N; k++)
			sigmaE[k].resolvingStep.stop();
	}
	
	// resolveInitiater, limit is the failing round
	public void resolveInitiator(int limit, HashMap<ElGamalKey, String> uris){
		
		SigmaEstablisher[] sigmaE = new SigmaEstablisher[N];
		
		for (int k=1; k<N; k ++)
			sigmaE[k] = new SigmaEstablisher(u[k].getKey(), trentK, uris);
		
		sigmaE[0] = new SigmaEstablisherFailer(u[0].getKey(), trentK, uris, limit, false);
		
		for (int k=0; k<N; k++){
			sigmaE[k].initialize(c[k]);
		}

		try{
			Thread.sleep(1000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for (int k=0; k<N; k++){
			sigmaE[k].start();
		}
		
		// Time to realize procedure
		try{
			Thread.sleep(5000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int k=0; k<N; k++)
			sigmaE[k].resolvingStep.stop();
	}
 
	// Test an abort in protocol (Trent doesn't give the signature)
	@Test
	public void TestB(){
		resolveInitiator(1, uris);
		
		boolean res = true;
		for (int k=0; k<N; k++){
			res =  res && c[k].isFinalized();
			assertTrue(c[k].getStatus().equals(Status.CANCELLED));
		}
		
		assertFalse(res);
	}
	
	// Test a resolve in protocol (Trent gives the signature in the end)
	@Test
	public void TestC(){
		resolveInitiator(2, uris);
		
		boolean res = true;
		for (int k=0; k<N; k++){
			res =  res && c[k].isFinalized();
			assertTrue(c[k].getStatus().equals(Status.FINALIZED));
		}

		assertTrue(res);
	}
}
