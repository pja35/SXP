package model.syncManager;

import java.util.Collection;

import crypt.api.hashs.Hasher;
import crypt.factories.HasherFactory;
import model.api.UserSyncManager;
import model.entity.User;

public class UserSyncManagerImpl extends AbstractSyncManager<User> implements UserSyncManager{
	public UserSyncManagerImpl() {
		super();
		this.initialisation("persistence", User.class);
	}

	@Override
	public User getUser(String username, String password) {
		Collection<User> users = this.findAllByAttribute("nick", username);
		for(User u: users) {
			Hasher hasher = HasherFactory.createDefaultHasher();
			hasher.setSalt(u.getSalt());
			//check if passwords are the sames
			String hash1 = new String(u.getPasswordHash());
			String hash2 = new String(hasher.getHash(password.getBytes()));
			if(hash1.equals(hash2)) {
				return u;
			}
		}
		return null;
	}
	
}
