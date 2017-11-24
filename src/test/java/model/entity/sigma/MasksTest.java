package model.entity.sigma;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import util.TestInputGenerator;

import java.math.BigInteger;

import static org.junit.Assert.assertTrue;

/**
 * Masks unit test
 *
 * @author denis.arrivault[@]univ-amu.fr
 */
public class MasksTest {
    private final static Logger log = LogManager.getLogger(MasksTest.class);
    Masks masks;
    private BigInteger a;
    private BigInteger aBis;

    @Before
    public void instantiate() {
        a = TestInputGenerator.getRandomBigInteger(100);
        aBis = TestInputGenerator.getRandomBigInteger(100);
        masks = new Masks(a, aBis);
    }

    @Test
    public void getterSetterTest() {

        assertTrue(masks.getA().equals(a));
        assertTrue(masks.getaBis().equals(aBis));

        a = TestInputGenerator.getRandomBigInteger(100);
        aBis = TestInputGenerator.getRandomBigInteger(100);

        masks.setA(aBis);
        masks.setaBis(a);

        assertTrue(masks.getA().equals(aBis));
        assertTrue(masks.getaBis().equals(a));

        log.debug(masks);
    }
}

