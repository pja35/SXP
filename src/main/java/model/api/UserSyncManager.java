package model.api;

import model.entity.User;

public interface UserSyncManager extends SyncManager<User>{
	/**
	 * Get an user according to username/password combo
	 * @param username
	 * @param password
	 * @return an User or null if no user/pass match
	 */
	public User getUser(String username, String password);
}
