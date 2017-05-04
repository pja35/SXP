package crypt.impl;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import crypt.api.hashs.Hasher;
import crypt.factories.HasherFactory;
import crypt.impl.hashs.SHA256Hasher;

public class TestCrypt {
	public static void main(String[] args){

		MessageDigest md = null;
		try {
			//Getting SHA-256 hashing instance
			md = MessageDigest.getInstance("SHA-256");
			md.reset();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		String texte = "azertya";
		byte[] byted=texte.getBytes();

		Hasher hashers = HasherFactory.createDefaultHasher();
		byte[] hashed = hashers.getHash(byted);
		
		SHA256Hasher hash2 = new SHA256Hasher();
		byte[] hashed2 = hash2.getHash(byted);

		System.out.println(MessageDigest.isEqual(hashed,hashed2));
		
		BigInteger test= new BigInteger(hashed);
		BigInteger test2= new BigInteger(hashed2);
		System.out.println(test + "\n" + test2);
		String hexNumber="237b526feacd4df6c222049b76ac77fef47be38044863d5a52210dc19e9c83d3";
		BigInteger decimal = new BigInteger(hexNumber, 16);
		System.out.println(decimal);
	}
}

