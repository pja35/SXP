/**
 * TestUtils.java created by denis.arrivault[@]univ-amu.fr
 */ 
package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.core.util.IOUtils;

/**
 * @author denis.arrivault[@]univ-amu.fr
 *
 */
public class TestUtils {
	private final static Logger log = LogManager.getLogger(TestUtils.class);


	public static boolean removeRecursively(File file) {

		log.debug("Removing " + file);

		if (file == null)
			return false;
		if (!file.exists())
			return true;
		if (file.isDirectory()){

			File[] contents = file.listFiles();
			if (contents != null) {
				for (File f : contents) {
					if (f.isDirectory())
						removeRecursively(f);
					else
						f.delete();
				}
			}
		}
		return file.delete();
	}

	public static boolean removePeerCache() {
		Path dir = Paths.get(".");
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path file: stream) {
				if (file.getFileName().toString().startsWith(".peer") && file.toFile().isDirectory()){
					removeRecursively(file.toFile());
				};
			}
		} catch (IOException | DirectoryIteratorException x) {
			log.error(x);
		}


		return true;
	}

	public static String get_https_cert(HttpsURLConnection con){
		StringBuffer buff = new StringBuffer();
		if(con!=null){

			try {
				buff.append("****** Certificate of the URL ********\n");
				buff.append("Response Code : " + con.getResponseCode() + '\n');
				buff.append("Cipher Suite : " + con.getCipherSuite() + '\n');
				buff.append("\n");
				InputStream is;
				if (con.getResponseCode() >= 400) {
					is = con.getErrorStream();
				} else {
					is = con.getInputStream();
				}
				InputStreamReader ri = new InputStreamReader(is);
				buff.append(IOUtils.toString(ri)); 
				Certificate[] certs = con.getServerCertificates();
				for(Certificate cert : certs){
					buff.append("Cert Type : " + cert.getType() + '\n');
					buff.append("Cert Hash Code : " + cert.hashCode() + '\n');
					buff.append("Cert Public Key Algorithm : "
							+ cert.getPublicKey().getAlgorithm() + '\n');
					buff.append("Cert Public Key Format : "
							+ cert.getPublicKey().getFormat() + '\n');
					buff.append("\n");
				}

			} catch (SSLPeerUnverifiedException e) {
				buff.append(e.toString() + '\n' + e.getMessage() + '\n');
				for(StackTraceElement el : e.getStackTrace()){
					buff.append(el.toString() + '\n');
				}
			} catch (IOException e){
				buff.append(e.toString() + '\n' + e.getMessage() + '\n');
				for(StackTraceElement el : e.getStackTrace()){
					buff.append(el.toString() + '\n');
				}
			}

		}
		return buff.toString();
	}

	public static String get_https_content(HttpsURLConnection con){
		StringBuffer buff = new StringBuffer();
		if(con!=null){

			try {

				buff.append("****** Content of the URL ********\n");
				BufferedReader br =	new BufferedReader(new InputStreamReader(con.getInputStream()));
				String input;
				while ((input = br.readLine()) != null){
					buff.append(input);
				}
				br.close();

			} catch (IOException e) {
				buff.append(e.toString() + '\n' + e.getMessage() + '\n');
				for(StackTraceElement el : e.getStackTrace()){
					buff.append(el.toString() + '\n');
				}
			}

		}
		return buff.toString();
	}
}
