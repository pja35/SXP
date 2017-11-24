package crypt.factories;

import crypt.api.signatures.Signer;
import crypt.impl.signatures.ElGamalSigner;
import crypt.impl.signatures.SigmaSigner;

/**
 * {@linkplain Signer} factory
 *
 * @author Julien Prudhomme
 */
public class SignerFactory {

    public static Signer<?, ?> createDefaultSigner() {
        return createElGamalSigner();
    }

    public static ElGamalSigner createElGamalSigner() {
        return new ElGamalSigner();
    }

    public static SigmaSigner createSigmaSigner() {
        return new SigmaSigner();
    }
}
