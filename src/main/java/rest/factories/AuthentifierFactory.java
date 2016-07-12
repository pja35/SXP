package rest.factories;

import rest.api.Authentifier;
import rest.impl.SimpleAuthentifier;

public class AuthentifierFactory {
	public static Authentifier createDefaultAuthentifier() {
		return createAuthentifier("simple");
	}
	
	public static Authentifier createAuthentifier(String impl) {
		switch(impl) {
		case "simple": return new SimpleAuthentifier();
		default: throw new RuntimeException("No implementation for " + impl);
		}
	}
}
