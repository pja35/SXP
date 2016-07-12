package crypt.impl.encryption;

import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

import Serpent.Serpent_BitSlice;
import crypt.api.encryption.Encrypter;

/**
 * Encrypter block symetric encryption with Serpent algorithm
 * @author Prudhomme Julien
 */
public class SerpentEncrypter implements Encrypter<byte[]>{
	private Object key; //key generated for serpent
	private static final int BLOCK_SIZE = 16; 
	
	/**
	 * Provide the nth block according to BLOCK_SIZE
	 * @param n a number between 0 and size of blocks / BLOCK_SIZE
	 * @return the nth block
	 */
	private byte[] getBlock(int n, byte[] blocks) {
		byte[] res = new byte[BLOCK_SIZE];
		int j = n*BLOCK_SIZE + BLOCK_SIZE;
		for(int i = n*BLOCK_SIZE, k=0; i < j; i++, k++) {
			//we add as much 0 as needed to fit the block
			if(i >= blocks.length) res[k] = 0;
			else res[k] = blocks[i];
		}
		return res;
	}
	
	/**
	 * Randomise a new initialisation vector for the encryption
	 * It is used for the first Xor operation
	 * @return a random init vector
	 */
	private byte[] getRandomInitVector() {
		byte[] res = new byte[BLOCK_SIZE];
		Random r = new SecureRandom();
		r.nextBytes(res);
		return res;
	}
	
	
	/**
	 * encrypt the result of Xor between encrypted previous block (or Init Vector) and the current block
	 * @param previous The previous (already) encrypted block or an init vector
	 * @param current the current block
	 * @return encrypted block
	 */
	private byte[] encrytBlock(byte[] previous, byte[] current) {
		byte[] res = new byte[BLOCK_SIZE];
		for(int i = 0; i < BLOCK_SIZE; i++) {
			res[i] = (byte) (previous[i] ^ current[i]);
		}
		return Serpent_BitSlice.blockEncrypt(res, 0, key);
	}
	
	private byte[] decryptBlock(byte[] previous, byte[] current) {
		byte[] res = Serpent_BitSlice.blockDecrypt(current, 0, key);
		for(int i = 0; i < BLOCK_SIZE; i++) {
			res[i] = (byte) (res[i] ^ previous[i]);
		}
		return res;
	}
	
	@Override
	public byte[] encrypt(byte[] plainText) {
		if(this.key == null) {
			throw new RuntimeException("key not defined");
		}
		ArrayList<byte[]> blocks = new ArrayList<>();
		blocks.add(getRandomInitVector());
		for(int i = 0; i * BLOCK_SIZE < plainText.length; i++) {
			blocks.add(this.encrytBlock(blocks.get(blocks.size()-1), this.getBlock(i, plainText)));
		}
		//allocate enough bytes for the cipher, initvector, and the number of 0 added
		byte[] res = new byte[blocks.size() * BLOCK_SIZE + 1];
		// we keep how much 0 were added at the end
		res[res.length-1] = (byte) ((blocks.size() - 1)*BLOCK_SIZE - plainText.length);
		int k = 0;
		for(byte[] b: blocks) { //we put encryption result in a single byte array
			for(int i = 0; i < b.length; i++) {
				res[k] = b[i];
				k++;
			}
		}
		return res;
	}

	@Override
	public byte[] decrypt(byte[] cypher) {
		if(this.key == null) {
			throw new RuntimeException("key not defined");
		}
		byte added = cypher[cypher.length - 1]; //retriving how much 0 were added
		//allocate the right size (remove added 0, init vector, and the byte used to keep added
		byte[] res = new byte[cypher.length - added - BLOCK_SIZE - 1];
		int k = 0;
		for(int i = 1; i*BLOCK_SIZE < cypher.length - 1; i++) {
			byte[] plainText = decryptBlock(this.getBlock(i-1, cypher), this.getBlock(i, cypher));
			for(int j = 0; j < plainText.length && k < res.length; j++, k++) {
				res[k] = plainText[j];
			}
		}
		return res;
	}
	
	
	
	/**
	 * Set the key (password) for the encryption
	 * @param key The password
	 */
	public void setKey(String key) {
		setKey(key.getBytes());
	}

	@Override
	public void setKey(byte[] key) {
		try {
			//generate the key with Serpent
			this.key = Serpent_BitSlice.makeKey(key);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}
}
