package crypt.impl.encryption;

import org.bouncycastle.crypto.engines.ElGamalEngine;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;

import crypt.api.encryption.Encrypter;
import crypt.impl.key.ElGamalAsymKey;
import model.entity.ElGamalKey;

/**
 * El Gamal implementation for encryption. Use {@link ElGamalAsymKey} as key
 * @author Prudhomme Julien
 *
 */
public class ElGamalEncrypter implements Encrypter<ElGamalKey>{

	private ElGamalKey key;
	
	@Override
	public void setKey(ElGamalKey key) {
		this.key = key;
	}

	@Override
	public byte[] encrypt(byte[] plainText) {
		if(this.key == null) {
			throw new RuntimeException("key not defined");
		}
		ElGamalParameters params = new ElGamalParameters(key.getP(), key.getG());
		ElGamalPublicKeyParameters pubKey = new ElGamalPublicKeyParameters(key.getPublicKey(), params);
		
		ElGamalEngine e = new ElGamalEngine();
		e.init(true, pubKey);
        return e.processBlock(plainText, 0, plainText.length);
	}

	@Override
	public byte[] decrypt(byte[] cypher) {
		if(this.key == null) {
			throw new RuntimeException("key not defined");
		}
		ElGamalParameters params = new ElGamalParameters(key.getP(), key.getG());
		ElGamalPrivateKeyParameters privKey = new ElGamalPrivateKeyParameters(key.getPrivateKey(), params);
		
		ElGamalEngine e = new ElGamalEngine();
		e.init(false, privKey);
		
        return e.processBlock(cypher, 0, cypher.length) ;
	}

}
