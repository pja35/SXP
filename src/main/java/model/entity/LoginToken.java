package model.entity;

import javax.persistence.Entity;

@Entity
public class LoginToken {
	private String token;
	private String userid;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String l) {
		this.userid = l;
	}
}
