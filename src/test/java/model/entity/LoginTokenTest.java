package model.entity;

import org.junit.Before;
import org.junit.Test;
import util.TestInputGenerator;

import javax.persistence.Entity;

import static org.junit.Assert.assertTrue;

/**
 * LoginToken unit tests
 *
 * @author denis.arrivault[@]univ-amu.fr
 */
public class LoginTokenTest {

    LoginToken loginToken;
    String token;
    String userid;

    @Before
    public void instantiate() {
        loginToken = new LoginToken();
        token = TestInputGenerator.getRandomIpsumText();
        userid = TestInputGenerator.getRandomUser();
        loginToken.setToken(token);
        loginToken.setUserid(userid);
    }

    @Test
    public void gettersTest() {
        assertTrue(loginToken.getToken().equals(token));
        assertTrue(loginToken.getUserid().equals(userid));
    }

    @Test
    public void classAnnotationsTest() {
        assertTrue(loginToken.getClass().getAnnotation(Entity.class) != null);
    }
}
