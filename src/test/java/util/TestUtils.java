/**
 * TestUtils.java created by denis.arrivault[@]univ-amu.fr
 */ 
package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
		if (!file.isDirectory())
			return false;

		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				if (f.isDirectory())
					removeRecursively(f);
				else
					f.delete();
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
}
