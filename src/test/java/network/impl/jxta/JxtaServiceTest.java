package network.impl.jxta;


import net.jxta.endpoint.Message;
import network.api.Messages;
import network.api.service.InvalidServiceException;
import network.factories.PeerFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.rules.ExpectedException;
import util.TestInputGenerator;
import util.TestUtils;

import java.io.File;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * JxtaService unit tests
 *
 * @author denis.arrivault[@]univ-amu.fr
 */
public class JxtaServiceTest {
    private final static Logger log = LogManager.getLogger(JxtaServiceTest.class);
    static private String cache = TestInputGenerator.getRandomCacheName();
    static private JxtaPeer jxtaPeer;
    @Rule
    public ExpectedException exception = ExpectedException.none();
    JxtaService jxtaService;

    @BeforeClass
    public static void setUpClass() {
        TestUtils.removeRecursively(new File(cache));
        jxtaPeer = PeerFactory.createJxtaPeer();
        try {
            jxtaPeer.start(cache, 9800);
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDownClass() {
        jxtaPeer.stop();
        TestUtils.removeRecursively(new File(cache));
    }

    @Before
    public void initialize() {
        jxtaService = new JxtaService();
    }

    @Test
    public void addServiceToPeer() throws InvalidServiceException {
        exception.expect(InvalidServiceException.class);
        exception.expectMessage("Service name is empty");
        jxtaPeer.addService(jxtaService);
    }

    @Test
    public void badInitTest() throws InvalidServiceException {
        exception.expect(RuntimeException.class);
        exception.expectMessage("Need a Jxta Peer to run a Jxta service");
        jxtaService.initAndStart(null);
    }

    @Test
    public void noNameTest() {
        assertNull(jxtaService.getName());
    }

    @Test
    public void messageTest() {
        Messages messages = jxtaService.toMessages(new Message());
        assertNull(messages.getWho());
        String[] names = messages.getNames();
        // Message exists ...
        assertTrue(names.length == 1);
        // with null name.
        assertNull(names[0]);
    }
}

