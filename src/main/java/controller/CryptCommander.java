package controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import crypt.api.hashs.Hasher;
import crypt.factories.HasherFactory;
import rest.api.ServletPath;

@ServletPath("/command/hash/*")
@Path("/")
public class CryptCommander {
	
	@GET
	@Path("/{input}")
	@Produces(MediaType.APPLICATION_JSON)
	public String hash(@PathParam("input") String input) {
		Hasher hasher = HasherFactory.createDefaultHasher();
		return new String(hasher.getHash(input.getBytes()));
	}
}
