package util;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.svenjacobs.loremipsum.LoremIpsum;

public class TestInputGenerator {

	private final static Logger log = LogManager.getLogger(TestInputGenerator.class);

	private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_~`!@#$%^&*()-=+[]|;:,<.>/";
	private static final LoremIpsum ipsum = new LoremIpsum();
	private static final Random r = new Random();

	/**
	 * Return a random integer x st min <= x < max 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int getRandomInt(int min, int max){
		int retint = r.nextInt(max-min) + min;
		log.debug(min + " <= " + retint + " < " + max);
		return retint;
	}

	/**
	 * Return a random extract of lipsum text with a number of words nbw s.t. 
	 * 1 <= nbw < nbwordmax.
	 * The beginning of the text is randomly chosen.  
	 * @param nbwordmax
	 * @return
	 */
	public static String getRandomIpsumText(int nbwordmax) {
		//get random indices for lipsum text (number of words and position)
		int wordsNumber = r.nextInt(nbwordmax) + 1;
		int pos = r.nextInt(50);
		int sum = wordsNumber + pos;
		if (sum >= 50){
			//scale to 50
			double scale = 1d * 50 / sum;
			wordsNumber = (int)(wordsNumber * scale);
			pos = (int)(pos * scale);
		}
		String retText = ipsum.getWords(wordsNumber, pos);
		log.debug("Number of words : " + wordsNumber);
		log.debug("Start position : " + pos);
		log.debug("Text : " + retText);
		return retText;
	}

	/**
	 * Return a random extract of lipsum text with a number of words nbw s.t. 
	 * 1 <= nbw < 20.
	 * The beginning of the text is randomly chosen.
	 * @return String
	 */
	public static String getRandomIpsumText() {
		return getRandomIpsumText(20);
	}

	/**
	 * Private method. Don't use it.
	 */
	private static String getRandomIpsumSubstring(int nbChar){
		if (nbChar >= LoremIpsum.LOREM_IPSUM.length())
			return "";
		int pos = r.nextInt(LoremIpsum.LOREM_IPSUM.length() - nbChar);
		String substr = LoremIpsum.LOREM_IPSUM.substring(pos, pos + nbChar);
		log.debug("Substring size : " + nbChar);
		log.debug("Substring: " + substr);
		return substr;
	}

	/**
	 * Return a random extract of lipsum text with a number of characters nbc s.t. 
	 * nbc == nbChar exactly.
	 * The beginning of the text is randomly chosen.
	 * @param nbChar. If nbChar is greater than the total number of lipsum text characters, 
	 * the extract restarts at the beginning.
	 * @return
	 */
	public static String getRandomIpsumString(int nbChar){
		String title = "";
		int ipsLength = LoremIpsum.LOREM_IPSUM.length();
		for(int i = 0; i < nbChar / ipsLength; ++i){
			title += LoremIpsum.LOREM_IPSUM;
		}
		title += getRandomIpsumSubstring(nbChar % ipsLength);
		log.debug("Text size : " + nbChar);
		log.debug("Text : " + title);
		return title;
	}

	/**
	 * Return a random user name with nbChar characters.
	 * @param nbChar
	 * @return
	 */
	public static String getRandomUser(int nbChar){
		String un = RandomStringUtils.random( nbChar, characters.substring(0,63) );
		log.debug("User size : " + nbChar);
		log.debug("User : " + un);
		return un;
	}

	/**
	 * Return a random user name with a number of characters nbc s.t.
	 * 6 <= nbc < 26
	 * @param nbChar
	 * @return
	 */
	public static String getRandomUser(){
		return getRandomUser(6 + r.nextInt(20));
	}

	/**
	 * Return a random word containing only letters (can be capital) with nbChar characters.
	 * @param nbChar
	 * @return
	 */
	public static String getRandomAlphaWord(int nbChar){
		String un = RandomStringUtils.random( nbChar, characters.substring(0,52) );
		log.debug("AlphaWord size : " + nbChar);
		log.debug("AlphaWord : " + un);
		return un;
	}

	/**
	 * Return a random word containing only letters (can be capital) with a number of characters nbc s.t.
	 * 6 <= nbc < 26
	 * @param nbChar
	 * @return
	 */
	public static String getRandomAlphaWord(){
		return getRandomAlphaWord(6 + r.nextInt(20));
	}

	/**
	 * Return a random password with nbChar characters
	 * @param nbChar
	 * @return
	 */
	public static String getRandomPwd(int nbChar){
		String pd = RandomStringUtils.random( nbChar, characters );
		log.debug("Password size : " + nbChar);
		log.debug("Password : " + pd);
		return pd;
	}

	/**
	 * Return a random password with a number of characters nbc s.t.
	 * 6 <= nbc < 26
	 * @return
	 */
	public static String getRandomPwd(){
		return getRandomPwd( 6 + r.nextInt(20));
	}

	/**
	 * Return a random cache directory name starting with ".cache"
	 * @return
	 */
	public static String getRandomCacheName(){
		String cache = ".cache_" + RandomStringUtils.random( 20, characters.substring(0,63) );
		log.debug("Cache size : " + cache.length());
		log.debug("Cache : " + cache);
		return cache;
	}
	
	/**
	 * Return a random persistance db directory name starting with ".db-"
	 * @return
	 */
	public static String getRandomDbName(){
		String cache = ".db-" + RandomStringUtils.random( 20, characters.substring(0,63) );
		log.debug("DB size : " + cache.length());
		log.debug("DB : " + cache);
		return cache;
	}

	/**
	 * Return a random non zero big integer with maxBitNb bits max. 
	 * @param maxBitNb
	 * @return
	 */
	public static BigInteger getRandomNotNullBigInteger(int maxBitNb){
		int b = r.nextInt(maxBitNb) + 1;
		if (b == 1){
			log.debug("BigInteger (1 bits) : 1");
			return new BigInteger("1");
		} 
		BigInteger retbi = new BigInteger("0");
		while(retbi.longValue() == 0){
			retbi = new BigInteger(b, new Random());
		}
		log.debug("BigInteger (" + b + " bits) : " + retbi);
		return retbi;
	}

	/**
	 * Return a random big integer with a number of bits nb s.t.
	 * minBitNb <= nb < maxBitNb
	 * @param minBitNb
	 * @param maxBitNb
	 * @return
	 */
	public static BigInteger getRandomBigInteger(int minBitNb, int maxBitNb){
		int b = r.nextInt(maxBitNb - minBitNb) + minBitNb;
		BigInteger retbi = new BigInteger(b, new Random());
		log.debug("BigInteger (" + b + " max bits) : " + retbi);
		return retbi;
	}

	/**
	 * Return a random big integer with maxBitNb bits max. 
	 * @param maxBitNb
	 * @return
	 */
	public static BigInteger getRandomBigInteger(int maxBitNb){
		return getRandomBigInteger(1, maxBitNb);
	}

	/**
	 * Return a byte array with a size s s.t.
	 * 1 <= s < maxSize
	 * @param maxSize
	 * @return
	 */
	public static byte[] getRandomBytes(int maxSize){
		int size = r.nextInt(maxSize) + 1;
		byte[] retb = new byte[size];
		r.nextBytes(retb);
		log.debug(byteToString(retb));
		return retb;
	}
	
	/**
	 * Return the string tht corresponds to the input byte array
	 * @param in
	 * @return
	 */
	public static String byteToString(byte[] in){
		StringBuffer buff = new StringBuffer();
		buff.append("bytes [" + in.length + "] : {");
		for(byte b : in){buff.append(b + ", ");}
		buff.append("}");
		return buff.toString();
	}

	/**
	 * Return a string that represents the today date with the the given format
	 * @param format. ex : "dd/mm/yyyy"
	 * @return
	 */
	public static String getFormatedTodayDate(String format){
		DateFormat dateFormat = new SimpleDateFormat(format);
		Date date = new Date();
		log.debug("Date format : " + format);
		log.debug(dateFormat.format(date));
		return dateFormat.format(date);
	}

	public static Date getTodayDate(){
		return new Date();
	}
}
