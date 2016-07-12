package rest.impl;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

import rest.api.Authentifier;

public class SimpleAuthentifier implements Authentifier{

	private class Logins {
		public String login;
		public String password;
		
		public Logins(String login, String password) {
			this.login = login;
			this.password = password;
		}
	}
	
	private HashMap<String, Logins> tokens = new HashMap<>();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getToken(String username, String password) {
		//TODO check credential or juste store user password ?
		SecureRandom r = new SecureRandom();
		String token = new BigInteger(130, r).toString(32);
		while(tokens.containsKey(token)) {
			token = new BigInteger(130, r).toString(32);
		}
		tokens.put(token, new Logins(username, password));
		return token;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLogin(String token) {
		if(!tokens.containsKey(token)) {
			return null;
		}
		return tokens.get(token).login;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPassword(String token) {
		if(!tokens.containsKey(token)) {
			return null;
		}
		return tokens.get(token).password;
	}

	@Override
	public void deleteToken(String token) {
		tokens.remove(token);
	}

}
