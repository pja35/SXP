package protocol.impl.contract;
import static org.junit.Assert.*;

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
import model.entity.ElGamalKey;
import protocol.impl.contract.ElGamalContract;
import protocol.impl.sigma.ElGamalTest;
import util.TestInputGenerator;
import protocol.api.Wish;

/**
 * ElGamalContract unit test
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class ElGamalContractTest {
	private final static Logger log = LogManager.getLogger(ElGamalContractTest.class);
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
	private ElGamalContract contract;
	private String text;
	private Clauses clauses;

	@Before
	public void instantiate(){
		text = TestInputGenerator.getRandomIpsumText();
		clauses = new Clauses(text);
		contract = new ElGamalContract(clauses);
	}

	@Test
	public void clausesGetterTest(){
		ElGamalContract newContract = new ElGamalContract();
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
		for(int i = 0; i<N; i++){
			ElGamalKey key = ElGamalAsymKeyFactory.create(false);
			contract.addParty(key);
		}
		assertFalse(contract.isFinalized());
		for(ElGamalKey key : contract.getParties()){
			assertTrue(key.getClass().getName().equals("model.entity.ElGamalKey"));
			ElGamalSigner signer = new ElGamalSigner();
			signer.setKey(ElGamalAsymKeyFactory.create(false));
			contract.addSignature(key, contract.sign(signer, null));
		}
		assertFalse(contract.isFinalized());
		assertFalse(contract.checkContrat(contract));
		assertFalse(contract.checkContrat(new ElGamalContract(new Clauses(TestInputGenerator.getRandomIpsumText()))));
	}

	@Test
	public void finalizedTest(){
		for(int i = 0; i<N; i++){
			ElGamalKey key = ElGamalAsymKeyFactory.create(false);
			contract.addParty(key);
		}
		for(ElGamalKey key : contract.getParties()){
			assertTrue(key.getClass().getName().equals("model.entity.ElGamalKey"));
			ElGamalSigner signer = new ElGamalSigner();
			signer.setKey(key);
			contract.addSignature(key, contract.sign(signer, null));
		}
		assertTrue(contract.isFinalized());
		assertTrue(contract.checkContrat(contract));
		assertFalse(contract.checkContrat(new ElGamalContract(new Clauses(TestInputGenerator.getRandomIpsumText()))));
	}

	@Test
	public void getSetWishTest(){
		contract.setWish(Wish.ACCEPT);
		assertTrue(contract.getWish().compareTo(Wish.ACCEPT) == 0);
	}
}
