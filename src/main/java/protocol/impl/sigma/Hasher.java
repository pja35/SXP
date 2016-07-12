/* Copyright 2015 Pablo Arrighi, Sarah Boukris, Mehdi Chtiwi, 
   Michael Dubuis, Kevin Perrot, Julien Prudhomme.

   This file is part of SXP.

   SXP is free software: you can redistribute it and/or modify it 
   under the terms of the GNU Lesser General Public License as published 
   by the Free Software Foundation, version 3.

   SXP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
   PURPOSE.  See the GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License along with SXP. 
   If not, see <http://www.gnu.org/licenses/>. */
package protocol.impl.sigma;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is used for hash a message
 * @author Michael
 *
 */
public class Hasher {	
	public static String SHA256(byte[] bytes){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        byte[] mdbytes = md.digest();
        return Hexa.bytesToHex(mdbytes);
	}
	
	public static String SHA256(byte[] bytes, byte[] salt){
        MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(salt);
			md.update(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        byte[] mdbytes = md.digest();
        return Hexa.bytesToHex(mdbytes);
	}
	
	public static String SHA256(String string) {
        MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        byte[] mdbytes = md.digest();
        return Hexa.bytesToHex(mdbytes);
    }
	
	public static String SHA256(String passWord, String salt){
        MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(salt.getBytes("UTF-8"));
			md.update(passWord.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        byte[] mdbytes = md.digest();
        return Hexa.bytesToHex(mdbytes);
	}
	
	public static void main(String[] args) {
		String test = "test";
		System.out.println(Hasher.SHA256(test));
	}
}
