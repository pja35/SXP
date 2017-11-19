package protocol.impl.sigma;

import org.junit.Before;
import org.junit.Test;
import util.TestInputGenerator;

import java.math.BigInteger;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * ElGamalEncrypt unit test
 *
 * @author denis.arrivault[@]univ-amu.fr
 */
public class ElGamalEncryptTest {
    ElGamalEncrypt encrypt;
    private BigInteger u;
    private BigInteger v;
    private BigInteger k;
    private byte[] m;

    @Before
    public void instantiate() {
        u = TestInputGenerator.getRandomBigInteger(100);
        v = TestInputGenerator.getRandomBigInteger(100);
        k = TestInputGenerator.getRandomBigInteger(100);
        m = TestInputGenerator.getRandomBytes(100);
        encrypt = new ElGamalEncrypt(u, v, k, m);
    }

    @Test
    public void gettertest() {
        assertTrue(u.equals(encrypt.getU()));
        assertTrue(v.equals(encrypt.getV()));
        assertTrue(k.equals(encrypt.getK()));
        assertTrue(Arrays.equals(m, encrypt.getM()));
    }

    @Test
    public void setMTest() {
        m = TestInputGenerator.getRandomBytes(100);
        assertFalse(Arrays.equals(m, encrypt.getM()));
        encrypt.setM(m);
        assertTrue(Arrays.equals(m, encrypt.getM()));
    }
}

