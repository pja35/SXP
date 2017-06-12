package crypt.impl.certificate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.security.KeyPair;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * X509V3Generator unit tests
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class X509V3GeneratorTest {
	@Rule public ExpectedException exception = ExpectedException.none();
	private final static Logger log = LogManager.getLogger(X509V3GeneratorTest.class);
		
	static private String config_file;



	static private X509V3Generator gen;
	
	@BeforeClass
	public static void setUpClass() {
		log.debug("**************** Starting test");
	}

	@Before
	public void instantiate(){
		config_file = "test.conf";
		log.debug("Instanciate");
		try {
			gen = X509V3Generator.getInstance(config_file);
		} catch (Exception e) {
			fail("Fail to instantiate X509V3Generator.");
			log.error(e.getMessage());
		}
	}

	@After
	public void deleteConfigFile(){
		File cf = new File(config_file);
		if (cf.exists()){
			cf.delete();
		}
	}

	@AfterClass
	static public void deleteConfFiles(){
		File folder = new File(".");
		for(File f : folder.listFiles()){
			if(f.getName().endsWith("test.conf") || f.getName().endsWith("test.jks")){
				f.delete();
			}
		}
	}

	@Test
	public void testBadInitFile() throws Exception{
		String content = "foo=foo\n";
		File badFile = new File("bad_test.conf");
		try {
			badFile.createNewFile();
			FileWriter file_writer = new FileWriter(badFile);
			file_writer.write (content);
			file_writer.close();
		} catch (IOException e) {
			fail(e.getMessage());
		}
		exception.expect(RuntimeException.class);
		exception.expectMessage("Bad configuration file : foo=foo");	
		gen = X509V3Generator.getInstance("bad_test.conf");
	}

	@Test
	public void testBadInitFile2() throws Exception{
		File badFile = new File("bad2_test.conf");
		try {
			badFile.createNewFile();
		} catch (IOException e) {
			fail(e.getMessage());
		}
		gen = X509V3Generator.getInstance("bad2_test.conf");
//		Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
//		perms.add(PosixFilePermission.OWNER_READ);
//		perms.add(PosixFilePermission.GROUP_READ);
//		perms.add(PosixFilePermission.OTHERS_READ);		
//		Files.setPosixFilePermissions(Paths.get("bad2_test.conf"), perms);
		badFile.delete();
		badFile.mkdir();
		exception.expect(IOException.class);
		exception.expectMessage("Error while creation of default configuration file : bad2_test.conf");	
		gen.createDefaultConfigFile();
	}

	@Test
	public void testGetInstance() {
		assertNotNull(gen);
	}

	@SuppressWarnings("unused")
	@Test
	public void testGetKeysPairException() {
		exception.expect(RuntimeException.class);
		exception.expectMessage("getKeyPair() used wihout certificate genererated");	
		KeyPair kp = gen.getKeysPair();		
	}

	@Test
	public void testCreateCertificateAndGetters() {
		try {
			assertTrue(gen.CreateCertificate("self-signed") == gen.CreateChainCertificate()[0]);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		KeyPair kp = gen.getKeysPair();
		assertTrue(kp.getPrivate().getAlgorithm().equals("RSA"));
		try {
			assertTrue(gen.getKsPassword().equals("123456"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testCreateCertificateException() throws Exception {
		String badSignatureName = "badname";
		exception.expect(RuntimeException.class);
		exception.expectMessage("Unknown Signature process : " + badSignatureName);
		gen.CreateCertificate(badSignatureName);
	}

	@Test
	public void testCreateChainCertificate() {
		try {
			assertTrue(gen.CreateChainCertificate()[0] == gen.CreateCertificate("self-signed"));
		} catch (Exception e) {
			fail(e.getMessage());
		}		
	}
	
	@Test
	public void testStoreInKeystore(){
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		PrintStream stdout = System.out;
		System.setOut(new PrintStream(outContent));
		try {
			gen.CreateCertificate("self-signed");
			File kf = new File("keystore_test.jks");
			if(kf.exists())
				kf.delete();
			kf.createNewFile();
			gen.StoreInKeystore("keystore_test.jks");
			assertEquals("Keystore already exist\n", outContent.toString());
			kf.delete();
			gen.StoreInKeystore("keystore_test.jks");
			assertTrue(kf.exists());
		} catch (Exception e) {
			fail(e.getMessage());
		} finally{
			System.setOut(stdout);
		}		
	}
}
