package crypt.api.certificate;

import java.security.KeyPair;
import java.security.cert.Certificate;

/**
 * Use to create Certificate (objet and file (jks)
 * @author Sébastien Pelletier
 *
 */
public interface CertificateGenerator 
{
	/**
	 * Get the keyPair used for the certificate.
	 * @author Sébastien Pelletier
	 */
	public KeyPair getKeysPair() throws Exception;

	/**
	 * Get the Keystore password
	 * @author Sébastien Pelletier
	 */
	public String getKsPassword() throws Exception;
	
	/**
	 * Set the configuration file. (containing certificate information).
	 * @param file Path to the configuration file.
	 * @author Sébastien Pelletier
	 */
	public void setConfigFile(String file) throws Exception;

	/**
	 * Initalization of datas from file.
	 * @author Sébastien Pelletier
	 */
	public void initDatas() throws Exception;
	
	/**
	 * Create the certificate and keys for it.
	 * @param Signature specifie what signature you want.
	 * @return Return the newly created certificate.
	 * @author Sébastien Pelletier, Antoine Boudermine
	 */
	public Certificate CreateCertificate(String signature) throws Exception;

	/**
	 * Create a certificate chain of one certificate.
	 * @author Sébastien Pelletier
	 */
	public Certificate[] CreateChainCertificate() throws Exception;

	/**
	 * Store the created certificate in the specified file (keystore).
	 * @param file_name The keystore file.
	 * #Author Pelletier Sébastien
	 */
	public void StoreInKeystore(String file_name) throws Exception;


}
