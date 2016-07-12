package rest.util;

import java.lang.reflect.Field;
import java.util.Collection;

@Deprecated
public class JsonUtils {
	/**
	 * Static class
	 */
	private JsonUtils() {};
	
	/**
	 * Create a JSON string from an instance object.
	 * The object should be a Bean
	 * @param o an object.
	 * @return a String representation of this object.
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static String BeanStringify(Object o) {
		StringBuffer buff = new StringBuffer("{");
		boolean started = false;
		for(Field field: o.getClass().getDeclaredFields()) {
			if(started) {
				buff.append(", ");
			} else {
				started = true;
			}
			field.setAccessible(true);
			buff.append("\"" + field.getName() + "\": ");
			Object value;
			try {
				value = field.get(o);
				if(value instanceof String) {
					buff.append("\"" + (String) value + "\"");
				}
				else if(value instanceof Collection<?>) {
					buff.append(collectionStringify((Collection<?>) value));
				}
				else if(value == null) {
					buff.append("null");
				}
				else {
					buff.append("\"" + value + "\"");
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		buff.append("}");
		return buff.toString();
	}
	
	public static String collectionStringify(Collection<?> o) {
		StringBuffer buff = new StringBuffer("[");
		boolean started = false;
		for(Object e: o) {
			if(started) {
				buff.append(", ");
			} else {
				started = true;
			}
			buff.append(BeanStringify(e));
		}
		buff.append("]");
		return buff.toString();
	}
	
}
