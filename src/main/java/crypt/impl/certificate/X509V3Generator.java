package crypt.impl.certificate;

import java.net.URI;
import java.util.Arrays;

import javax.swing.JOptionPane;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.shredzone.acme4j.challenge.Challenge;
import org.shredzone.acme4j.challenge.Dns01Challenge;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeConflictException;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.exception.AcmeUnauthorizedException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.CertificateUtils;
import org.shredzone.acme4j.util.KeyPairUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.Registration;
import org.shredzone.acme4j.RegistrationBuilder;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Status;



import crypt.api.certificate.CertificateGenerator;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import java.security.Security;
import java.math.BigInteger;
import java.util.Date;

import java.security.cert.Certificate;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509Certificate;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.*;


public class X509V3Generator implements CertificateGenerator
{
	/**
	 * Use this methode to create an instance of X509V3Genertor.
	 * @param config_file Name of the file that will be used by the instance.
	 */
	static public X509V3Generator getInstance(String config_file) throws Exception
	{
		X509V3Generator cert_gen = new X509V3Generator();
		cert_gen.setConfigFile(config_file);
		cert_gen.initDatas();
		return cert_gen;
	}
	///////////////////////////// private //////////////////////////////////

	private String config_file;        //Configuration file (certificate datas).
	private boolean flag = false;	   //To kown if a certifictae has already been created.

	private String keystore_password;  //Password for the keystore. can use toCharArray()
	private String ks_alias;
	private KeyStore ks;               //KeyStore that will contain the certificate.
	private Certificate cert;          //The certificate.
	private KeyPair key_pair;          //Generated keys for the certificate.

	//Certificate datas.
	private BigInteger serial_number;
	private String domain_name;		   //Server's IP.
	//private Date begin;              //Start validity date.
	//private Date end;                //End validity date.
	private String sign_alg;           //Algorithm used to sign.
	

	////////////////////////////// Public ////////////////////////////////
	
	//// Geters&Seters ////


	/**
	 * {@inheritDoc}
	 */
	@Override
	public KeyPair getKeysPair() throws Exception
	{
		if( !this.flag )
		{
			System.out.println("getKeyPair() used wihout certificate genereated");
			System.exit(1);
		}
		return key_pair;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getKsPassword() throws Exception
	{
		return keystore_password;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setConfigFile(String file) throws Exception
	{
		this.config_file = file;
	}
	
	///// Methodes /////

	/**
	 * {@inheritDoc}
	 * file pattern : 
	 * data=value
	 * data1=value1
	 */
	@Override
	public void initDatas() throws Exception
	{
		try
		{
			File file = new File(this.config_file);
			
			//Create file if it does not exists.
			if( !file.exists() )
			{
				createDefaultConfigFile(this.config_file);
			}

			InputStream input_stream              = new FileInputStream(file); 
			InputStreamReader input_stream_reader = new InputStreamReader(input_stream);
			BufferedReader buffered_reader        = new BufferedReader(input_stream_reader);
			String line;

			String name;	//Configuration name.
			String value;   //Value to this name.
			String[] temp;
			

			while( (line = buffered_reader.readLine()) != null )
			{
				if( !line.isEmpty() && !line.startsWith("#") && !line.startsWith(" ") ) //if not a comment or newline.
				{
					line = line.split("#")[0];  //Delete comments.
					line = line.split(" ")[0];  //Delete useless spaces.
					line = line.split("\t")[0]; //Delete useless tabs.
					temp = line.split("=");
					name = temp[0];
					value = temp[1];
					switch(name)
					{
						case "kspassword" :
							this.keystore_password = value;
							break;
						case "ksalias":
							this.ks_alias = value;
							break;
						case "serialnumber" :
							this.serial_number = new BigInteger(value);
							break;
						case "signalgorithm":
							this.sign_alg = value;
							break;
						case "domainname":
							this.domain_name = value;
							break;
						default:
							System.out.println("Bad configuration file : " + line);
							System.exit(1);
						break;
					}
					//System.out.println(name + " = " + value);
				}
			}

			buffered_reader.close(); 
		}		
		catch( Exception e )
		{
			System.out.println(e.toString());
			System.exit(1);
		}
		
	}

	/**
	 * Create the default certificate configuration file.
	 * @param file_name Name of the file that will be created.
	 */
	public void createDefaultConfigFile(String file_name) throws Exception
	{
		String content = "";
		content += "# This configuration file contains values for the certificate\n";
		content += "# used by this application. The certificate will be stored in\n";
		content += "# keystore.jks .\n\n";
		content += "kspassword=123456        #Password for the keystore.\n";
		content += "ksalias=SXP              #Alias for the certificate in the keystore.\n";
		content += "serialnumber=0123456789  #Serial Number.\n";
		content += "signalgorithm=MD5WithRSA #Algorithm used for siging the certificate. (SHA256withRSA)\n";
		content += "domainname=localhost     #IP / domain name of the serveur.\n";

		File file = new File(this.config_file);
		try
		{
			FileWriter file_writer = new FileWriter(file);
			file_writer.write (content);
			file_writer.close();
		}
		catch (IOException exception)
		{
			System.out.println ("Error while creation of default configuration file : " 
								+ this.config_file + "\n" + exception.getMessage() );
		}
	}

	/**
	 * {@inheritDoc}
	 * Implemented Signature : "self-signed"
	 * 						   "CA-signed"
	 */
	@Override
	public Certificate CreateCertificate(String signature) throws Exception
	{
		if( signature == "self-signed" )
		{
			if( !this.flag )
			{
				//Provider custom
				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

				//Keys (priv & public) generation.																							
				KeyPairGenerator key_gen = KeyPairGenerator.getInstance("RSA");															
				key_gen.initialize(1024);																												
				KeyPair keys = key_gen.genKeyPair();
				this.key_pair = keys;

				//Création du certificat.																												
				X509V3CertificateGenerator cert_gen = new X509V3CertificateGenerator();

				X500Principal cn = new X500Principal("CN=" + domain_name );																		
				cert_gen.setSerialNumber(this.serial_number);																		
				cert_gen.setIssuerDN(cn);																												
				cert_gen.setNotBefore(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));									
				cert_gen.setNotAfter(new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000));							
				cert_gen.setSubjectDN(cn);																												
				cert_gen.setPublicKey(keys.getPublic());																							
				cert_gen.setSignatureAlgorithm(this.sign_alg);
	
				this.cert = cert_gen.generateX509Certificate(keys.getPrivate(), "BC"); //CA private key (autosigned)
				this.flag = true;
			}
		}
		else if( signature == "CA-signed" )
		{
			//Provider custom
			
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

			//Keys (priv & public) generation.
																							
			KeyPairGenerator key_gen = KeyPairGenerator.getInstance("RSA");															
			key_gen.initialize(2048);																												
			KeyPair keys = key_gen.genKeyPair();
			this.key_pair = keys;

			// Create a session for Let's Encrypt

			Session session = new Session("acme://letsencrypt.org/staging", this.key_pair);

			// Register a new user

			Registration reg = null;
			try 
			{
				reg = new RegistrationBuilder().create(session);
			} 
			catch (AcmeConflictException ex) 
			{
				reg = Registration.bind(session, ex.getLocation());
				System.out.println("Let's Encrypt account does already exist");
			}

			URI agreement = reg.getAgreement();
			reg.modify().setAgreement(agreement).commit();

			Authorization auth = null;
			try 
			{
				auth = reg.authorizeDomain(domain_name);
			} 
			catch (AcmeUnauthorizedException ex)
			{
				// Maybe there are new T&C to accept?
				reg.modify().setAgreement(agreement).commit();
				// Then try again...
				auth = reg.authorizeDomain(domain_name);
			}

			Challenge challenge = httpChallenge(auth, domain_name);
			if (challenge == null) 
			{
				System.exit(0);
			}
			System.out.println("challenge created");
			challenge.trigger();
			
			int attempts = 10;
			while (challenge.getStatus() != Status.VALID && attempts-- > 0) 
			{
				System.out.println(attempts);
				if (challenge.getStatus() == Status.INVALID) 
				{
					System.out.println("Challenge failed... Giving up.");
					System.exit(0);
				}
				try 
				{
					Thread.sleep(3000L);
				} 
				catch (InterruptedException ex) 
				{
					System.out.println("interrupted");
				}
				challenge.update();
			}
			if (challenge.getStatus() != Status.VALID) 
			{
				System.out.println("Failed to pass the challenge... Giving up.");
				System.exit(0);
			}
			System.out.println("challenge acepté");
			// Generate a CSR for the domain
			CSRBuilder csrb = new CSRBuilder();
			csrb.addDomains(domain_name);//peut etre une collection de string
			csrb.sign(key_pair);

			// Request a signed certificate
			org.shredzone.acme4j.Certificate certificate = reg.requestCertificate(csrb.getEncoded());

			// Download the certificate chain
			X509Certificate[] chain = certificate.downloadChain();
			this.cert = chain[0];
		}
		else
		{
			System.out.println("Unknown Signature process : " + signature);
			System.exit(1);
		}

		return this.cert;
	}


public Challenge httpChallenge(Authorization auth, String domain) throws AcmeException 
{

	// Find a single http-01 challenge
	Http01Challenge challenge = auth.findChallenge(Http01Challenge.TYPE);
	if (challenge == null) 
	{
		System.out.println("Found no http challenge, I don't know what to do...");
		return null;
	}

	try
	{
		FileWriter file = new FileWriter(new File("." + challenge.getToken() ));
		file.write (challenge.getAuthorization());
		file.close();
	}
	catch(IOException exp )
	{
		System.out.println("error");
	}
	
	// Output the challenge, wait for acknowledge...
	System.out.println("Please create a file in your web server's base directory.");
	System.out.println("It must be reachable at: http://" + domain_name + "/.well-known/acme-challenge/" + challenge.getToken());
	System.out.println("File name: " + challenge.getToken());
	System.out.println("Content: " + challenge.getAuthorization());
	System.out.println("The file must not contain any leading or trailing whitespaces or line breaks!");
	System.out.println("If you're ready, dismiss the dialog...");

	return challenge;
}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Certificate[] CreateChainCertificate() throws Exception
	{
		if( !this.flag )
			this.CreateCertificate("auto-signed");

		Certificate[] cert_chain = new X509Certificate[1];																		
		cert_chain[0] = this.cert;
		return cert_chain;
	}

	/**
	 * Store the created certificate in the specified file (keystore).
	 * @param file_name The keystore file.
	 * #Author Pelletier Sébastien
	 */
	public void StoreInKeystore(String file_name) throws Exception
	{	
		File file = new File(file_name);
		if( file.exists() )
			{
				System.out.println("Keystore already exist");
				return;
			}
																													
		char[] password = this.keystore_password.toCharArray();																				
		KeyStore.PasswordProtection protected_password = new KeyStore.PasswordProtection(password);	

		//Keystore Creation																												
		KeyStore ks = KeyStore.getInstance("jks");																						
		ks.load(null, password);  //Loading from nothing (null) in order to create a new one.
	
		Certificate[] cert_chain = this.CreateChainCertificate();

		ks.setEntry(this.ks_alias,																														
				new KeyStore.PrivateKeyEntry(key_pair.getPrivate(), cert_chain),														
				new KeyStore.PasswordProtection(password));

		//Storing the created key in the file.																						
		java.io.FileOutputStream fos = null;																								
		try 																																			
		{																																				
			fos = new java.io.FileOutputStream(file_name);																		
			ks.store(fos, password);																											
		}
		finally
		{
			if(fos != null)
				fos.close();
		}	
	}
}

