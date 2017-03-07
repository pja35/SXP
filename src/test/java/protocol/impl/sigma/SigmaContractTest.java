package protocol.impl.sigma;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import crypt.api.signatures.Signable;
import crypt.factories.ElGamalAsymKeyFactory;
import crypt.impl.signatures.ElGamalSignature;
import crypt.impl.signatures.ElGamalSigner;
import model.api.Wish;
import model.entity.ElGamalKey;
import protocol.impl.sigma.SigmaContract;
import util.TestInputGenerator;

/**
 * ElGamalContract unit test
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class SigmaContractTest {
	private final static Logger log = LogManager.getLogger(SigmaContractTest.class);
	@Rule public ExpectedException exception = ExpectedException.none();

	class Clauses implements Signable<ElGamalSignature> {
		private ElGamalSignature sign;
		private String text;

		public Clauses(String text) {
			this.text = text;
		}

		@Override
		public byte[] getHashableData() {
			return text.getBytes();
		}

		@Override
		public void setSign(ElGamalSignature s) {
			this.sign = s;
		}

		@Override
		public ElGamalSignature getSign() {
			return this.sign;
		}
	}

	private final int N = TestInputGenerator.getRandomInt(1, 20);
	private SigmaContract contract;
	private String text;
	private Clauses clauses;

	@Before
	public void instantiate(){
		text = TestInputGenerator.getRandomIpsumText();
		clauses = new Clauses(text);
		contract = new SigmaContract(clauses);
	}

	@Test
	public void clausesGetterTest(){
		SigmaContract newContract = new SigmaContract();
		newContract.setClauses(clauses);
		assertArrayEquals(newContract.getClauses().getHashableData(), clauses.getHashableData());
		assertArrayEquals(contract.getClauses().getHashableData(), clauses.getHashableData());
	}

	@Test
	public void addSignatureExceptionTest1(){
		exception.expect(RuntimeException.class);
		exception.expectMessage("invalid key");
		ElGamalKey key = ElGamalAsymKeyFactory.create(false);
		ElGamalSigner signer = new ElGamalSigner();
		signer.setKey(key);
		contract.addSignature(key, contract.sign(signer, null));
	}

	@Test
	public void addSignatureExceptionTest2(){
		exception.expect(RuntimeException.class);
		exception.expectMessage("invalid key");
		ElGamalKey key = ElGamalAsymKeyFactory.create(false);
		ElGamalSigner signer = new ElGamalSigner();
		signer.setKey(key);
		contract.addSignature(null, contract.sign(signer, null));
	}

	@Test
	public void badFinalizationTest(){
		ArrayList<ElGamalKey> parties = new ArrayList<ElGamalKey>();
		for(int i = 0; i<N; i++){
			ElGamalKey key = ElGamalAsymKeyFactory.create(false);
			parties.add(key);
		}
		contract.setParties(parties, true);
		assertFalse(contract.isFinalized());
		for(ElGamalKey key : contract.getParties()){
			assertTrue(key.getClass().getName().equals("model.entity.ElGamalKey"));
			ElGamalSigner signer = new ElGamalSigner();
			signer.setKey(ElGamalAsymKeyFactory.create(false));
			contract.addSignature(key, contract.sign(signer, null));
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
		contract.setParties(parties, true);
		for(ElGamalKey key : contract.getParties()){
			assertTrue(key.getClass().getName().equals("model.entity.ElGamalKey"));
			ElGamalSigner signer = new ElGamalSigner();
			signer.setKey(key);
			contract.addSignature(key, contract.sign(signer, null));
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
}
