package util;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.svenjacobs.loremipsum.LoremIpsum;

public class TestInputGenerator {

	private final static Logger log = LogManager.getLogger(TestInputGenerator.class);

	private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_~`!@#$%^&*()-=+[]|;:,<.>/";
	private static final LoremIpsum ipsum = new LoremIpsum();
	private static final Random r = new Random();
	
	public static int getRandomInt(int min, int max){
		int retint = r.nextInt(max-min) + min;
		log.debug(min + " <= " + retint + " < " + max);
		return retint;
	}

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

	public static String getRandomIpsumText() {
		return getRandomIpsumText(20);
	}
	
	private static String getRandomIpsumSubstring(int nbChar){
		if (nbChar >= LoremIpsum.LOREM_IPSUM.length())
			return "";
		int pos = r.nextInt(LoremIpsum.LOREM_IPSUM.length() - nbChar);
		String substr = LoremIpsum.LOREM_IPSUM.substring(pos, pos + nbChar);
		log.debug("Substring size : " + nbChar);
		log.debug("Substring: " + substr);
		return substr;
	}
	
	public static String getRandomIpsumString(int nbChar){
		String title = "";
		int ipsLength = LoremIpsum.LOREM_IPSUM.length();
		for(int i = 0; i < nbChar / ipsLength; ++i){
			title += LoremIpsum.LOREM_IPSUM;
		}
		title += getRandomIpsumSubstring(nbChar % ipsLength);
		log.debug("Title size : " + nbChar);
		log.debug("Title : " + title);
		return title;
	}
	
	public static String getRandomUser(int nbChar){
		String un = RandomStringUtils.random( nbChar, characters.substring(0,63) );
		log.debug("User size : " + nbChar);
		log.debug("User : " + un);
		return un;
	}
	
	public static String getRandomUser(){
		return getRandomUser(6 + r.nextInt(20));
	}
	
	public static String getRandomAlphaWord(int nbChar){
		String un = RandomStringUtils.random( nbChar, characters.substring(0,52) );
		log.debug("AlphaWord size : " + nbChar);
		log.debug("AlphaWord : " + un);
		return un;
	}
	
	public static String getRandomAlphaWord(){
		return getRandomAlphaWord(6 + r.nextInt(20));
	}
	
	public static String getRandomPwd(int nbChar){
		String pd = RandomStringUtils.random( nbChar, characters );
		log.debug("Password size : " + nbChar);
		log.debug("Password : " + pd);
		return pd;
	}
	
	public static String getRandomPwd(){
		return getRandomPwd( 6 + r.nextInt(20));
	}
	
	public static String getRandomCacheName(){
		String cache = ".cache_" + RandomStringUtils.random( 20, characters.substring(0,63) );
		log.debug("Cache size : " + cache.length());
		log.debug("Cache : " + cache);
		return cache;
	}
	
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
	
	public static BigInteger getRandomBigInteger(int minBitNb, int maxBitNb){
		int b = r.nextInt(maxBitNb - minBitNb) + minBitNb;
		BigInteger retbi = new BigInteger(b, new Random());
		log.debug("BigInteger (" + b + " bits) : " + retbi);
		return retbi;
	}

	
	public static BigInteger getRandomBigInteger(int maxBitNb){
		return getRandomBigInteger(1, maxBitNb);
	}

	public static byte[] getRandomBytes(int maxSize){
		int size = r.nextInt(maxSize) + 1;
		byte[] retb = new byte[size];
		r.nextBytes(retb);
		log.debug(byteToString(retb));
		return retb;
	}
	
	public static String byteToString(byte[] in){
		StringBuffer buff = new StringBuffer();
		buff.append("bytes [" + in.length + "] : {");
		for(byte b : in){buff.append(b + ", ");}
		buff.append("}");
		return buff.toString();
	}
	
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
