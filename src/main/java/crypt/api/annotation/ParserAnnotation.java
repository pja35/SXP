package crypt.api.annotation;

import java.math.BigInteger;

import crypt.api.key.AsymKey;

/**
 * Instances of objects that implements ParserAnnotation can parse object with CryptAnnotation. 
 * @author Radoua Abderrahim
 * 
 * @param <Entity> Type of Entity (object). 
 */
public interface ParserAnnotation<Entity> {
	
	/**
	 * this method perform some actions on Entity like Crypt, Hash, Sign ... actions, and return the Entity itself.
	 * @param actions : Enumeration of ParserAction to specify witch action can be performed on Entity, it can be an array of actions.
	 * @return Entity : return the same object passed in Parser constructor  
	 */
	public Entity parseAnnotation(ParserAction ...actions);
	
	
	/**
	 * set key as ElGamalKey, this key contains a pair of public and private key, they will be used in Crypt, Hash, Sign ... actions
	 * @param key
	 */
	public void setKey(AsymKey<BigInteger> key);
}
