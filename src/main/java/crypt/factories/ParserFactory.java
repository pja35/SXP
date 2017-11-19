package crypt.factories;

import crypt.api.annotation.ParserAnnotation;
import crypt.api.key.AsymKey;
import crypt.utils.CryptoParser;

import java.math.BigInteger;


/**
 * @author Radoua Abderrahim
 */
public class ParserFactory {

    /**
     * Create the default implementation of {@link ParserAnnotation}
     *
     * @return a {@link ParserAnnotation}
     */
    public static ParserAnnotation createDefaultParser(Object entity, AsymKey<BigInteger> key) {
        return new CryptoParser(entity, key);
    }
}
