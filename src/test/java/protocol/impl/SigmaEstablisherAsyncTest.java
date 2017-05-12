package protocol.impl;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import controller.Application;
import controller.ApplicationForTests;
import crypt.api.hashs.Hasher;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.factories.HasherFactory;
import model.api.SyncManager;
import model.entity.ContractEntity;
import model.entity.ElGamalKey;
import model.entity.User;
import model.syncManager.UserSyncManagerImpl;
import network.api.EstablisherService;
import protocol.impl.sigma.SigmaContract;
import util.TestInputGenerator;
import util.TestUtils;

public class SigmaEstablisherAsyncTest {
	public static Application application;
	public static ApplicationForTests applicationForTests;
	public static final int restPort = 5601;
	
	public static final int N = 2;
	
	// Users
	private static User[] u;
	private static ElGamalKey[] keysR;
	private static ElGamalKey trentK = ElGamalAsymKeyFactory.create(false);

	// A contract for each signer
	private static SigmaContract[] c;
	// A contract entity
	private static ContractEntity[] ce;
	

	/*
	 * Create the users, the application
	 */
	@BeforeClass
	static public void initialize(){
		application = new Application();
		application.runForTests(restPort);

		applicationForTests = new ApplicationForTests();
		applicationForTests.runForTests(restPort);
		
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
		keysR = new ElGamalKey[N];		
		for (int k=0; k<N; k++){
			ElGamalKey key = u[k].getKey();
			keysR[k] = new ElGamalKey();
			keysR[k].setG(key.getG());
			keysR[k].setP(key.getP());
			keysR[k].setPublicKey(key.getPublicKey());
		}
	}

	@AfterClass
	static public void deleteBaseAndPeer(){
		TestUtils.removeRecursively(new File(".db-" + restPort + "/"));
		TestUtils.removePeerCache();
		application.stop();
		applicationForTests.stop();
	}
	
	
	@Before
	public void instantiate(){
		c = new SigmaContract[N];
		ce = new ContractEntity[N];
		
		// Initialize the contracts 
		ArrayList<String> cl = new ArrayList<String>();
		cl.add("clause 1");
		cl.add("second clause");
		

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
	
	@Test
	public void goodSigningTest(){
		SigmaEstablisherAsync[] sigmaE = new SigmaEstablisherAsync[N];
		for (int k=0; k<N; k++){
				sigmaE[k] = new SigmaEstablisherAsync(u[k].getKey(), trentK);
		}
		sigmaE[1].establisherService =(EstablisherService) ApplicationForTests.getInstance().getPeer().getService(EstablisherService.NAME);
		sigmaE[1].peer = ApplicationForTests.getInstance().getPeer();
		for (int k=0; k<N; k++){
			sigmaE[k].initialize(c[k]);
			sigmaE[k].start();
		}
		
		// Time to setup the passwords
		try{
			Thread.sleep(10000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
