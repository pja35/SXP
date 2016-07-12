package crypt.base;

import crypt.api.signatures.Signable;
import crypt.api.signatures.Signer;

/**
 * Abstract signer
 * @author Prudhomme Julien
 *
 * @param <S> type of signature
 * @param <K> type of key
 */
public abstract class AbstractSigner<S,K> implements Signer<S,K>{

	protected K key;
	
	@Override
	public void setKey(K key) {
		this.key = key;
	}

	@Override
	public S sign(Signable<S> signable) {
		S sign = this.sign(signable.getHashableData());
		signable.setSign(sign);
		return sign;
	}

	@Override
	public boolean verify(Signable<S> signable) {
		return verify(signable.getHashableData(), signable.getSign());
	}

}
