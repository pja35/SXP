package rest.api;

public interface Authentifier {
	
	public static final String PARAM_NAME = "Auth-Token";
	/**
	 * Generate a token for an username/password
	 * @param username
	 * @param password
	 * @return
	 */
	public String getToken(String username, String password);
	
	/**
	 * Get login from token
	 * @param token
	 * @return
	 */
	public String getLogin(String token);
	
	/**
	 * Get password from token
	 * @param token
	 * @return
	 */
	public String getPassword(String token);

	/**
	 * Delete a token (for logout)
	 * @param token
	 */
	public void deleteToken(String token);
}
