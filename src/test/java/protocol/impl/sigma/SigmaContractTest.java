package protocol.impl.sigma;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.core.type.TypeReference;

import controller.Users;
import controller.tools.JsonTools;
import crypt.api.signatures.Signable;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.impl.signatures.SigmaSigner;
import model.api.Status;
import model.api.Wish;
import model.entity.ContractEntity;
import model.entity.ElGamalKey;
import model.entity.User;
import model.entity.sigma.SigmaSignature;
import protocol.impl.sigma.SigmaContract;
import util.TestInputGenerator;

/**
 * ElGamalContract unit test
 * @author denis.arrivault[@]univ-amu.fr
 * @author nathanael.eon[@]lif.univ-mrs.fr
 *
 */
public class SigmaContractTest {
	@Rule public ExpectedException exception = ExpectedException.none();

	class Clauses implements Signable<SigmaSignature> {
		private SigmaSignature sign;
		private String text;

		public Clauses(String text) {
			this.text = text;
		}

		@Override
		public byte[] getHashableData() {
			return text.getBytes();
		}

		@Override
		public void setSign(SigmaSignature s) {
			this.sign = s;
		}

		@Override
		public SigmaSignature getSign() {
			return this.sign;
		}
	}
	
	private final int N = TestInputGenerator.getRandomInt(1, 20);
	private SigmaContract contract;
	private SigmaContract contract2;
	private ContractEntity contractE;
	private String text;
	private Clauses clauses;
	private ArrayList<String> cl = new ArrayList<String>();
	private ArrayList<ElGamalKey> keys;
	
	@Before
	public void instantiate(){
		text = TestInputGenerator.getRandomIpsumText();
		clauses = new Clauses(text);
		contract = new SigmaContract(clauses);
		contractE = new ContractEntity();
		contractE.setParties(new ArrayList<String>());
		contractE.setSignatures(new HashMap<String,String>());
		contractE.setClauses(new ArrayList<String>());
		contract2 = new SigmaContract(contractE);
	}

	@Test
	public void equalsTest(){
		SigmaContract contractBis = new SigmaContract();
		assertFalse(contract.equals(contractBis));
		contractBis.setClauses(clauses);
		assertTrue(contract.equals(contractBis));
	}
	
	@Test
	public void clausesGetterTest(){
		contract2.setClauses(clauses);
		assertArrayEquals(contract2.getClauses().getHashableData(), clauses.getHashableData());
		contract2.setClauses(cl);
		contract.setClauses(cl);
		assertArrayEquals(contract2.getClauses().getHashableData(), contract.getClauses().getHashableData());
	}

	@Test
	public void setPartiesTest(){
		JsonTools<Collection<User>> json = new JsonTools<>(new TypeReference<Collection<User>>(){});
		Users users = new Users();
		Collection<User> u = json.toEntity(users.get());
		ArrayList<String> ids = new ArrayList<String>();
		keys = new ArrayList<ElGamalKey>(); 
		for (User user : u){
			ids.add(user.getId());
			keys.add(user.getKey());
		}
		contract.setParties(ids);
		contract2.setParties(ids);
		assertTrue(contract.getParties().toString().equals(contract2.getParties().toString()));
	}
	
	@Test
	public void addSignatureExceptionTest1(){
		exception.expect(RuntimeException.class);
		exception.expectMessage("invalid key");
		ElGamalKey key = ElGamalAsymKeyFactory.create(false);
		SigmaSigner signer = new SigmaSigner();
		signer.setTrentK(ElGamalAsymKeyFactory.create(false));
		signer.setReceiverK(ElGamalAsymKeyFactory.create(false));
		contract.addSignature(key, contract.sign(signer, key));
	}

	@Test
	public void addSignatureExceptionTest2(){
		exception.expect(RuntimeException.class);
		exception.expectMessage("invalid key");
		ElGamalKey key = ElGamalAsymKeyFactory.create(false);
		SigmaSigner signer = new SigmaSigner();
		signer.setTrentK(ElGamalAsymKeyFactory.create(false));
		signer.setReceiverK(ElGamalAsymKeyFactory.create(false));
		contract.addSignature(null, contract.sign(signer, key));
	}

	@Test
	public void badFinalizationTest(){
		ArrayList<ElGamalKey> parties = new ArrayList<ElGamalKey>();
		for(int i = 0; i<N; i++){
			ElGamalKey key = ElGamalAsymKeyFactory.create(false);
			parties.add(key);
		}
		contract.setParties(parties, true);
		SigmaSigner signer = new SigmaSigner();
		signer.setTrentK(ElGamalAsymKeyFactory.create(false));
		/* It doesn't matter who is the receiver (in signature protocol, we need it
			to forge one part of the "OR" */
		signer.setReceiverK(ElGamalAsymKeyFactory.create(false));
		assertFalse(contract.isFinalized());
		for(ElGamalKey key : contract.getParties()){
			assertTrue(key.getClass().getName().equals("model.entity.ElGamalKey"));
			ElGamalKey k = ElGamalAsymKeyFactory.create(false);
			contract.addSignature(key, contract.sign(signer, k));
		}
		assertFalse(contract.isFinalized());
		assertFalse(contract.checkContrat(contract));
		assertFalse(contract.checkContrat(new SigmaContract(new Clauses(TestInputGenerator.getRandomIpsumText()))));
	}

	@Test
	public void finalizedTest(){
		ArrayList<ElGamalKey> parties = new ArrayList<ElGamalKey>();
		for(int i = 0; i<N; i++){
			ElGamalKey key = ElGamalAsymKeyFactory.create(false);
			parties.add(key);
		}
		ElGamalKey trentK = ElGamalAsymKeyFactory.create(false);
		ElGamalKey receiverK = ElGamalAsymKeyFactory.create(false);
		contract.setParties(parties, true);
		contract.setTrentKey(trentK);
		
		SigmaSigner signer = new SigmaSigner();
		signer.setTrentK(trentK);
		/* It doesn't matter who is the receiver (in signature protocol, we need it
			to forge one part of the "OR" */
		signer.setReceiverK(receiverK);
		
		for(ElGamalKey key : contract.getParties()){
			assertTrue(key.getClass().getName().equals("model.entity.ElGamalKey"));
			contract.addSignature(key, contract.sign(signer, key));
		}
		assertTrue(contract.isFinalized());
		assertTrue(contract.checkContrat(contract));
		assertFalse(contract.checkContrat(new SigmaContract(new Clauses(TestInputGenerator.getRandomIpsumText()))));
	}

	@Test
	public void getSetWishTest(){
		contract.setWish(Wish.ACCEPT);
		assertTrue(contract.getWish().compareTo(Wish.ACCEPT) == 0);
	}
	
	@Test
	public void getSetStatusTest(){
		contract.setStatus(Status.NOWHERE);
		assertTrue(contract.getStatus().compareTo(Status.NOWHERE) == 0);
	}
}
