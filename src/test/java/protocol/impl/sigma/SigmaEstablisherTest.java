package protocol.impl.sigma;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import controller.Application;
import crypt.factories.ElGamalAsymKeyFactory;
import model.entity.ElGamalKey;

public class SigmaEstablisherTest {
	
	public static final int N = 2;
		
	@Test
	public void test(){
		// Starting the Application to be able to test it
		if (Application.getInstance()==null){
			new Application();
			Application.getInstance().runForTests(8081);
		}

		//Initialize the keys
		ElGamalKey[] keys = new ElGamalKey[N];
		ElGamalKey[] keysR = new ElGamalKey[N];
		// Creating the contracts 
		ArrayList<String> cl = new ArrayList<String>();
		cl.add("hi");cl.add("hi2");
		ElGamalClauses signable1 = new ElGamalClauses(cl);
		SigmaContractAdapter[] c = new SigmaContractAdapter[N];
		
		// Creating the map of URIS
		String uri = Application.getInstance().getPeer().getUri();
		HashMap<ElGamalKey, String> uris = new HashMap<ElGamalKey, String>();
		HashMap<ElGamalKey, String> pwds = new HashMap<ElGamalKey, String>();
		

		for (int k=0; k<N; k++){
			keys[k] = ElGamalAsymKeyFactory.create(false);
			keysR[k] = new ElGamalKey();
			keysR[k].setG(keys[k].getG());
			keysR[k].setP(keys[k].getP());
			keysR[k].setPublicKey(keys[k].getPublicKey());
			
			uris.put(keysR[k], uri);
			pwds.put(keysR[k], String.valueOf(k));
		}
		
		for (int k=0; k<N; k++){
			c[k] = new SigmaContractAdapter();
			c[k].setClauses(signable1);
			for (int i=0; i<N; i++){
				c[k].addParty(keysR[i]);
			}
		}
		SigmaEstablisher[] sigmaE = new SigmaEstablisher[N];
		
		for (int k=0; k<N; k++){
			sigmaE[k] = new SigmaEstablisher(c[k], keys[k], uris);
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
}
