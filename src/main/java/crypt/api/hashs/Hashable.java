package crypt.api.hashs;

/**
 * Object that can provide an unique hash from his data can implement the Hashable interface.
 * @author Prudhomme Julien
 *
 */
public interface Hashable {
	
	/**
	 * Provide an unique byte array representing the object data. Equals object should
	 * provide the same byte array.
	 * For example if o1.equals(o2) is true, o1.getHashableData should be equal to o2.getHashableData.
	 * You can for example concatenate string value of all object attributes (always in the same order) and extract
	 * the bytes.
	 * @return byte[] An array representing object's data.
	 */
	public byte[] getHashableData();
}
