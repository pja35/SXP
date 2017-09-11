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
import model.api.Status;
import model.api.SyncManager;
import model.api.Wish;
import model.entity.ContractEntity;
import model.entity.ElGamalKey;

import model.entity.ElGamalSignEntity;
import model.entity.LoginToken;

import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import protocol.impl.sigma.SigmaContract;

import rest.api.Authentifier;
import util.TestInputGenerator;

import protocol.impl.sigma.Trent;
import util.TestInputGenerator;
import util.TestUtils;


public class SigmaEstablisherTest {
	public static final boolean useMessages = false;
	public static final int N = 2;
	
	public static Application application;
	public static final int restPort = 5602;
	public static HashMap<ElGamalKey, Trent> trents = new HashMap<ElGamalKey, Trent>();
	
	// Users
	private static User[] u;
	private static ElGamalKey[] keysR;
	// Map of URIS
	private static HashMap<ElGamalKey, String> uris;

	// A contract for each signer
	private static SigmaContract[] c;
	// A contract entity
	private static ContractEntity[] ce;
	// Clauses of contracts
	private static ArrayList<String> cl = new ArrayList<String>();
	
	@BeforeClass
	public static void setup(){
		application = new Application();
		application.runForTests(restPort);
		
		// Setup at least one Trent
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
		
		trents.put(u.getKey(), new Trent(u.getKey()));
		trents.get(u.getKey()).setListener();
		
	}


	@AfterClass
	public static void stopApp(){
		TestUtils.removeRecursively(new File(".db-" + restPort + "/"));
		TestUtils.removePeerCache();
		application.stop();
	}
	
	
	/*
	 * Create the users
	 */
	@Before
	public void initialize(){	
		
		// Initialize the users
		u = new User[N+1];
		for (int k=0; k<N+1; k++){
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
			
			ElGamalSignEntity s = new ElGamalSignEntity();
			s.setR(TestInputGenerator.getRandomBigInteger(100));
			s.setS(TestInputGenerator.getRandomBigInteger(100));
			u[k].setSignature(s);
			
			SyncManager<User> em = new UserSyncManagerImpl();
			em.begin();
			em.persist(u[k]);
			em.end();
			
			trents.put(u[k].getKey(), new Trent(u[k].getKey()));
			trents.get(u[k].getKey()).setListener();
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
	}
	
	/*
	 * Function used to setup the contracts with different numbers of contract
	 */
	private void setupContracts(int N){
		c = new SigmaContract[N];
		ce = new ContractEntity[N];
		
		// Initialize the contracts 
		cl.add(TestInputGenerator.getRandomIpsumText(100));
		cl.add(TestInputGenerator.getRandomIpsumText(100));
		

		ArrayList<String> parties = new ArrayList<String>();
		for(int i = 0; i<N; i++){
			parties.add(u[i].getId());
		}

		for (int k=0; k<N; k++){
			ce[k] = new ContractEntity();
			ce[k].setParties(parties);
			ce[k].setClauses(cl);
			ce[k].setSignatures(new HashMap<String, String>());
			ce[k].setWish(Wish.ACCEPT);
			c[k] = new SigmaContract(ce[k]);
		}
	}
	
	
	// Test a simple signing protocol
	@Test
	public void TestA(){
		setupContracts(N);
		SigmaEstablisher[] sigmaE = new SigmaEstablisher[N];
		for (int k=0; k<N; k++){
			sigmaE[k] = new SigmaEstablisher(u[k].getKey(), uris);
			sigmaE[k].initialize(c[k]);
		  	sigmaE[k].start();
		}
		
		// Time to realize procedure
		try{
			Thread.sleep(1000);
		}catch (InterruptedException e) {
			e.printStackTrace();
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
	private void resolveInitiator(int N, int limit, HashMap<ElGamalKey, String> uris){
		setupContracts(N);
		SigmaEstablisher[] sigmaEs = new SigmaEstablisher[N];

		sigmaEs[0] = new SigmaEstablisherFailer(u[0].getKey(), uris, limit, false);
		for (int k=1; k<N; k ++)
			sigmaEs[k] = new SigmaEstablisher(u[k].getKey(), uris);
		
		for (int k=0; k<N; k++){
			sigmaEs[k].initialize(c[k]);
			sigmaEs[k].start();
		}
		
		// Time to realize procedure
		try{
			Thread.sleep(3000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int k=0; k<N; k++){
			if (sigmaEs[k].resolvingStep == null)
				System.out.println("STEP : " + sigmaEs[k].sigmaEstablisherData.getProtocolStep().getRound());
			sigmaEs[k].resolvingStep.stop();
		}
	}
 
	// Test an abort in protocol (Trent doesn't give the signature)
	@Test
	public void TestB(){
		resolveInitiator(N, 1, uris);
		
		boolean res = false;
		for (int k=0; k<N; k++)
			if (c[k].getStatus().equals(Status.CANCELLED) || c[k].getStatus().equals(Status.RESOLVING))
				res = true;
		assertTrue(res);
	}
	
	// Test a resolve in protocol (Trent gives the signature in the end)
	@Test
	public void TestC(){
		resolveInitiator(N, 2, uris);

		boolean res = false;
		for (int k=0; k<N; k++)
			if (c[k].getStatus().equals(Status.FINALIZED) || c[k].getStatus().equals(Status.RESOLVING))
				res = true;
		assertTrue(res);
	}
	
	/*
	 * I dishonest and shows it
	 */
	@Test
	public void TestD(){
		int N = 2;
		setupContracts(N);
		SigmaEstablisher[] sigmaEs = new SigmaEstablisher[N];

		sigmaEs[0] = new SigmaEstablisherFailer(u[0].getKey(), uris, 1, 2);
		for (int k=1; k<N; k ++)
			sigmaEs[k] = new SigmaEstablisherFailer(u[k].getKey(), uris, N+3, true);
		
		for (int k=0; k<N; k++){
			sigmaEs[k].initialize(c[k]);
			sigmaEs[k].start();
		}
		
		// Time to realize procedure
		try{
			Thread.sleep(1000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int k=0; k<N; k++)
			sigmaEs[k].resolvingStep.stop();

		boolean res = true;
		for (int k=0; k<N; k++){
			res =  res && c[k].isFinalized();
			assertTrue(c[k].getStatus().equals(Status.FINALIZED));
		}
		
		assertTrue(res);
	}
	
	/*
	 *  Test when everyone lie, and when the communication with Trent don't seem to work (worse scenario)
	 *  Because everyone lie, the contract is signed in the end
	 *  i dishonest j show it
	 */
	
	@Test
	public void TestE(){
		int N = 2;
		setupContracts(N);
		SigmaEstablisher[] sigmaEs = new SigmaEstablisher[N];
		
		for (int k=0; k<N; k ++)
			sigmaEs[k] = new SigmaEstablisherFailer(u[k].getKey(), uris, k+1, true);
		
		for (int k=0; k<N; k++){
			sigmaEs[k].initialize(c[k]);
			sigmaEs[k].start();
		}
		
		// Time to realize procedure
		try{
			Thread.sleep(2000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int k=0; k<N; k++)
			sigmaEs[k].resolvingStep.stop();

		boolean res = true;
		for (int k=0; k<N; k++){
			res =  res && c[k].isFinalized();
			assertTrue(c[k].getStatus().equals(Status.FINALIZED));
		}
		
		assertTrue(res);
	}
}
