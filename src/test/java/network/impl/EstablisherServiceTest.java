package network.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import crypt.api.key.AsymKey;
import network.api.EstablisherService;
import network.api.EstablisherServiceListener;
import network.api.Peer;
import network.factories.PeerFactory;
import util.TestInputGenerator;
import util.TestUtils;

/**
 * !!!! This test cannot be launched as a junit test !!!!!
 * 	We need to start 2 peers which JXTA won't allow for 
 * 	a single app, thus, we need to test it manually by
 *	starting 2 applications. Down is the code for the "main"
 * 	method
 * @author Nathanaël EON
 *
 */

public class EstablisherServiceTest {
	
	private class Key implements AsymKey<BigInteger>{
		private BigInteger pk;
		private BigInteger pbk;
		
		@Override
		public BigInteger getPublicKey() {
			return pbk;
		}
		@Override
		public BigInteger getPrivateKey() {
			return pk;
		}
		@Override
		public BigInteger getParam(String p) {
			return null;
		}
		@Override
		public void setPublicKey(BigInteger pbk) {
			this.pbk = pbk;
		}
		@Override
		public void setPrivateKey(BigInteger pk) {
			this.pk = pk;
		}
		
	}
	private static Peer[] peer;
	private static EstablisherService[] es;
	private static String[] rdvPeerIds = {"tcp://localhost:9803", "tcp://localhost:9804"};
	
	private Key[] key;
	private String field, title, data;
	private boolean isReceived;
	
	
	@BeforeClass
	public static void initialize(){
		peer = new Peer[2];
		peer[0] = PeerFactory.createDefaultAndStartPeerForTest(9803, rdvPeerIds);
		peer[1] = PeerFactory.createDefaultAndStartPeerForTest(9804, rdvPeerIds);
		
		es = new EstablisherService[2];
		es[0] = (EstablisherService) peer[0].getService(EstablisherService.NAME);
		es[1] = (EstablisherService) peer[1].getService(EstablisherService.NAME);
	}

	@AfterClass
	public static void stopApp(){
		TestUtils.removePeerCache();
	}
	
	@Before
	public void instantiate(){
		isReceived = false;

		field = "title";
		title = TestInputGenerator.getRandomIpsumText();
		data = TestInputGenerator.getRandomIpsumText();
		
		key = new Key[2];
		for (int k=0; k<2; k++){
			key[k] = new Key();
			key[k].setPrivateKey(TestInputGenerator.getRandomBigInteger(8));
			key[k].setPublicKey(TestInputGenerator.getRandomBigInteger(8));
		}
	}
	
	@Test
	public void testAdvertisementListening(){
		es[0].setListener(field, title, title + key[0].getPublicKey().toString(),new EstablisherServiceListener(){
			@Override
			public void notify(String title, String data, String key){
				isReceived = true;
			}
		}, false);
		
		es[1].sendContract(title, data, key[1].getPublicKey().toString(), peer[1], null);


		try{
			Thread.sleep(1000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue(isReceived);
	}
	
	@Test
	public void testMessage() {
		
		assertFalse(isReceived);
		
		HashMap<Key, String> uris = new HashMap<Key, String>();
		for (int k=0; k<2; k++){
			uris.put(key[k], peer[k].getUri());
		}
		
		es[1].setListener(field, title, title+key[1].getPublicKey().toString(), new EstablisherServiceListener(){
				@Override
				public void notify(String title, String data, String key){
					isReceived = true;
				}
		}, true);
		
		es[0].sendContract(title, data, key[0].getPublicKey().toString(), peer[0], uris);
		

		try{
			Thread.sleep(500);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue(isReceived);
	}
	
	// Sending before listener setup and trying to retrieve our own message
	@Test
	public void testAdvertAsync(){
		
		assertFalse(isReceived);

		es[1].sendContract(title, data, key[1].getPublicKey().toString(), peer[1], null);
		
		es[0].setListener(field, title, title + key[0].getPublicKey().toString(),new EstablisherServiceListener(){
			@Override
			public void notify(String title, String data, String key){
				isReceived = true;
			}
		}, false);
		
		try{
			Thread.sleep(1000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(isReceived);
		
		
		isReceived = false;
		es[1].sendContract(title, data, key[1].getPublicKey().toString(), peer[1], null);
		
		try{
			Thread.sleep(1000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(isReceived);	
		
		
		isReceived = false;
		es[1].setListener(field, title, title + key[1].getPublicKey().toString(),new EstablisherServiceListener(){
			@Override
			public void notify(String title, String data, String key){
				isReceived = true;
			}
		}, false);
		try{
			Thread.sleep(1000);
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertTrue(isReceived);	
		
		
	}
}
