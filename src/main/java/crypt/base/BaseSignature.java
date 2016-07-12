package crypt.base;

import java.lang.reflect.Field;

import crypt.api.signatures.ParamName;
import crypt.api.signatures.Signature;

public class BaseSignature<T> implements Signature<T>{

	@SuppressWarnings("unchecked")
	@Override
	public T getParam(String p) {
		for(Field f : getClass().getDeclaredFields()) {
			ParamName pn = f.getAnnotation(ParamName.class);
			if(pn != null && pn.value().equals(p)) {
				try {
					return (T) f.get(this);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					
				}
			}
		}
		throw new RuntimeException("Undefined param" + p);
	}

}
