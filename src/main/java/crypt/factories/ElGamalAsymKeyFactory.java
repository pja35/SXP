package crypt.factories;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ElGamalKeyPairGenerator;
import org.bouncycastle.crypto.generators.ElGamalParametersGenerator;
import org.bouncycastle.crypto.params.ElGamalKeyGenerationParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;

import model.entity.ElGamalKey;

/**
 * Factory to create ElGamal keys with given or random parameters.
 * Contain default pre-calculated parameters {@link ElGamalAsymKeyFactory#P} and {@link ElGamalAsymKeyFactory#G}
 * @author Prudhomme Julien
 *
 */
public class ElGamalAsymKeyFactory {
	
	
	private static SecureRandom R = new SecureRandom();
	
	//Defaults parameters
	public static final BigInteger P = new BigInteger ("124233341635855292420681698148845681014844866056212176632655173602444135581779341928584451946831820357622587249219477577145009300106828967466602146104562163160400103396735672041344557638270362523343149686623705761738910044071399582025053147811261321814632661084042311141045136246602979886564584763268994320823");
	public static final BigInteger G = new BigInteger ("57879985263161130068016239981615161174385902716647642452899971198439084259551250230041086427537114453738884538337956090286524329552098304591825815816298805245947460536391128315522193556464285417135160058086869161063941463490748168352401178939129440934609861888674726565294073773971086710395310743717916632171");
	
	/**
	 * Randomly generate El Gamal parameters
	 * @return ElGamalParameters instance containing P & G 
	 */
	private static ElGamalParameters generatePG() {
		ElGamalParameters params;
		ElGamalParametersGenerator apg;
		apg = new ElGamalParametersGenerator();
		apg.init(1024, 20, R);
		params = apg.generateParameters();
		return params;
	}
	
	/**
	 * Generate and set El Gamal public & private key according to parameters.
	 * @param k the ElGamalAsymKey instance to set
	 * @param params Params used for public and private key generation
	 */
	private static void generateElGamalKey(ElGamalKey k, ElGamalParameters params) {
		ElGamalKeyGenerationParameters elGP = new ElGamalKeyGenerationParameters(R,params);
		ElGamalKeyPairGenerator KeyPair = new ElGamalKeyPairGenerator();
		KeyPair.init(elGP);
		AsymmetricCipherKeyPair cipher1 = KeyPair.generateKeyPair();
		k.setPublicKey(((ElGamalPublicKeyParameters) cipher1.getPublic()).getY()); 
		k.setPrivateKey(((ElGamalPrivateKeyParameters)cipher1.getPrivate()).getX());
	}
	
	/**
	 * Create a new ElGamalAsymKey with a random public/private key according to p and g parameters
	 * @param param ElGamal parameters containing p/g
	 * @return a new ElGamalAsymKey instance
	 */
	public static ElGamalKey createFromParameters(ElGamalParameters params) {
		ElGamalKey k = new ElGamalKey();
		generateElGamalKey(k, params);
		k.setG(params.getG());
		k.setP(params.getP());
		return k;
	}
	
	/**
	 * Create a new ElGamalAsymKey with a random public/private key according to default or random parameters
	 * @param generateParams True - Generate random parameters. It can take several minutes.
	 *  False - Take default parameters {@link ElGamalAsymKeyFactory#P} and {@link ElGamalAsymKeyFactory#G}
	 * @return
	 */
	public static ElGamalKey create(boolean generateParams) {
		return createFromParameters(generateParams?(generatePG()):(new ElGamalParameters(P, G)));
	}
}
